package com.only.www.onlyseclabs.controller;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.only.www.onlyseclabs.entity.ApiResponse;
import com.only.www.onlyseclabs.entity.ContainerDTO;
import com.only.www.onlyseclabs.utils.DockerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/api/docker")
public class DockerController {

    private static final Logger logger = LoggerFactory.getLogger(DockerController.class);
    private DockerUtils dockerUtils;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private boolean isConnected = false;
    private String currentHost;
    
    // 添加一个线程安全的Map来存储待处理的容器配置
    private final ConcurrentHashMap<String, ContainerDTO> pendingBuilds = new ConcurrentHashMap<>();

    @PostMapping("/connect")
    public ResponseEntity<Map<String, Object>> connect(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String dockerHost = request.get("dockerHost");
            if (isConnected && dockerHost.equals(currentHost)) {
                response.put("success", true);
                response.put("message", "Already connected to " + dockerHost);
                return ResponseEntity.ok(response);
            }

            dockerUtils = new DockerUtils(dockerHost);
            // 测试连接
            dockerUtils.listContainers(true);

            isConnected = true;
            currentHost = dockerHost;
            response.put("success", true);
            response.put("message", "Successfully connected to " + dockerHost);
        } catch (Exception e) {
            isConnected = false;
            currentHost = null;
            dockerUtils = null;
            response.put("success", false);
            response.put("message", "Connection failed: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/connection")
    public ResponseEntity<Map<String, Object>> checkConnection() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", isConnected);
        if (isConnected) {
            response.put("host", currentHost);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/listContainers")
    public ResponseEntity<?> listContainers() {
        if (!isConnected) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Docker not connected");
            return ResponseEntity.ok(response);
        }
        try {
            return ResponseEntity.ok(dockerUtils.listContainers(true));
        } catch (Exception e) {
            // 如果出现异常，可能是连接已断开
            isConnected = false;
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to list containers: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    @GetMapping("/images")
    public ResponseEntity<ApiResponse<List<Image>>> listImages() {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        return ResponseEntity.ok(ApiResponse.success(dockerUtils.listImages()));
    }

    @PostMapping("/containers/create")
    public SseEmitter createContainer(@RequestBody ContainerDTO containerDTO) {
        if (!isConnected) {
            throw new RuntimeException("Docker not connected");
        }

        SseEmitter emitter = new SseEmitter(-1L); // 无超时
        
        executorService.execute(() -> {
            try {
                String containerId = dockerUtils.createContainerWithLogs(
                    containerDTO.getImageName(),
                    containerDTO.getContainerName(),
                    containerDTO.getPorts(),
                    log -> {
                        try {
                            emitter.send(SseEmitter.event()
                                .data(log)
                                .name("log"));
                        } catch (IOException e) {
                            logger.error("Error sending log", e);
                        }
                    }
                );

                // 发送完成消息
                emitter.send(SseEmitter.event()
                    .data("Container created successfully with ID: " + containerId)
                    .name("complete"));
                
                emitter.complete();
            } catch (Exception e) {
                logger.error("Error creating container", e);
                try {
                    emitter.send(SseEmitter.event()
                        .data("Failed to create container: " + e.getMessage())
                        .name("error"));
                } catch (IOException ex) {
                    logger.error("Error sending error message", ex);
                }
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @PostMapping("/containers/build")
    public ResponseEntity<Map<String, String>> buildContainer(@RequestBody ContainerDTO containerDTO) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Docker not connected"));
        }
        
        // 生成唯一ID
        String buildId = UUID.randomUUID().toString();
        pendingBuilds.put(buildId, containerDTO);
        
        Map<String, String> response = new HashMap<>();
        response.put("buildId", buildId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/containers/build/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamBuildLogs(@RequestParam String buildId) {
        SseEmitter emitter = new SseEmitter(-1L);
        
        executorService.execute(() -> {
            try {
                // 从Map中获取containerDTO
                ContainerDTO containerDTO = pendingBuilds.get(buildId);
                if (containerDTO == null) {
                    throw new RuntimeException("No container configuration found for build ID: " + buildId);
                }

                // 发送开始消息
                emitter.send(SseEmitter.event()
                    .data("开始构建...")
                    .name("log"));

                // 解析docker-compose内容
                String composeContent = containerDTO.getDockerfilePath();
                String dockerfileContent = convertComposeToDockerfile(composeContent);

                // 创建临时Dockerfile文件
                File dockerfileDir = Files.createTempDirectory("dockerfile_").toFile();
                File dockerfile = new File(dockerfileDir, "Dockerfile");
                
                // 写入Dockerfile内容
                Files.write(dockerfile.toPath(), 
                           dockerfileContent.getBytes(), 
                           StandardOpenOption.CREATE);
                
                logger.info("Created Dockerfile with content:\n{}", dockerfileContent);
                logger.info("Created temporary Dockerfile at: {}", dockerfile.getAbsolutePath());

                // 构建镜像并创建容器
                dockerUtils.createContainerFromDockerfileWithLogs(
                    dockerfile,
                    containerDTO.getContainerName(),
                    containerDTO.getPorts(),
                    log -> {
                        try {
                            emitter.send(SseEmitter.event()
                                .data(log)
                                .name("log"));
                        } catch (IOException e) {
                            logger.error("Error sending build log", e);
                        }
                    }
                );

                // 清理临时文件
                dockerfile.delete();
                dockerfileDir.delete();

                // 清理构建配置
                pendingBuilds.remove(buildId);

                // 发送完成消息
                emitter.send(SseEmitter.event()
                    .data("容器构建完成")
                    .name("complete"));
                    
                emitter.complete();
                
            } catch (Exception e) {
                logger.error("Error building container", e);
                try {
                    emitter.send(SseEmitter.event()
                        .data("构建失败: " + e.getMessage())
                        .name("error"));
                } catch (IOException ex) {
                    logger.error("Error sending error message", ex);
                }
                emitter.completeWithError(e);
                // 清理构建配置
                pendingBuilds.remove(buildId);
            }
        });
        
        return emitter;
    }

    private String convertComposeToDockerfile(String composeContent) {
        // 使用简单的字符串处理来提取信息
        // 这里假设输入的compose文件格式固定
        String[] lines = composeContent.split("\n");
        StringBuilder dockerfile = new StringBuilder();
        
        String imageName = null;
        Map<String, String> ports = new HashMap<>();
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("image:")) {
                imageName = line.substring("image:".length()).trim();
            } else if (line.matches("\\s*-\\s*\"\\d+:\\d+\"")) {
                // 提取端口映射，但在Dockerfile中我们不需要这个
                String portMapping = line.replaceAll("\\s*-\\s*\"", "").replaceAll("\"", "");
                String[] parts = portMapping.split(":");
                if (parts.length == 2) {
                    ports.put(parts[0], parts[1]);
                }
            }
        }
        
        if (imageName != null) {
            dockerfile.append("FROM ").append(imageName).append("\n");
            // 可以添加其他必要的Dockerfile指令
            dockerfile.append("EXPOSE ");
            ports.values().forEach(port -> dockerfile.append(port).append(" "));
            dockerfile.append("\n");
        } else {
            throw new RuntimeException("No image specified in docker-compose file");
        }
        
        return dockerfile.toString();
    }

    @DeleteMapping("/containers/{containerId}")
    public ResponseEntity<ApiResponse<Void>> removeContainer(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        dockerUtils.removeContainer(containerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/containers/{containerId}/stop")
    public ResponseEntity<ApiResponse<Void>> stopContainer(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        dockerUtils.stopContainer(containerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/containers/{containerId}/restart")
    public ResponseEntity<ApiResponse<Void>> restartContainer(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        dockerUtils.restartContainer(containerId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/containers/{containerId}/logs")
    public ResponseEntity<ApiResponse<String>> getContainerLogs(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        try {
            // 获取容器日志
            String logs = dockerUtils.getContainerLogs(containerId);
            return ResponseEntity.ok(ApiResponse.success(logs));
        } catch (Exception e) {
            logger.error("Error getting container logs: {}", containerId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to get container logs: " + e.getMessage()));
        }
    }

    @GetMapping("/containers/{containerId}/logs/stream")
    public SseEmitter streamContainerLogs(@PathVariable String containerId) {
        if (!isConnected) {
            throw new RuntimeException("Docker not connected");
        }

        SseEmitter emitter = new SseEmitter(-1L); // 无超时
        
        executorService.execute(() -> {
            try {
                dockerUtils.streamContainerLogs(containerId, log -> {
                    try {
                        emitter.send(SseEmitter.event()
                                .data(log)
                                .name("log"));
                    } catch (IOException e) {
                        logger.error("Error sending log", e);
                    }
                });
            } catch (Exception e) {
                logger.error("Error streaming container logs", e);
                try {
                    emitter.send(SseEmitter.event()
                            .data("Error: " + e.getMessage())
                            .name("error"));
                } catch (IOException ex) {
                    logger.error("Error sending error message", ex);
                }
                emitter.completeWithError(e);
            }
        });
        
        return emitter;
    }

    @GetMapping("/containers/{containerId}/status")
    public ResponseEntity<ApiResponse<Boolean>> isContainerRunning(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        return ResponseEntity.ok(ApiResponse.success(dockerUtils.isContainerRunning(containerId)));
    }

    @PostMapping("/containers/{containerId}/start")
    public ResponseEntity<ApiResponse<Void>> startContainer(@PathVariable String containerId) {
        if (!isConnected) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Docker not connected"));
        }
        try {
            dockerUtils.startContainer(containerId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            logger.error("Error starting container: {}", containerId, e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to start container: " + e.getMessage()));
        }
    }

} 