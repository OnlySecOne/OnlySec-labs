package com.only.www.onlyseclabs.utils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.LogContainerResultCallback;
import com.github.dockerjava.transport.DockerHttpClient;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class DockerUtils {
    private static final Logger logger = LoggerFactory.getLogger(DockerUtils.class);
    private final DockerClient dockerClient;
    private static final int TIMEOUT_MINUTES = 5;

    public DockerUtils(String dockerHost) {
        try {
            // 创建 Docker 客户端配置
            DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                    .withDockerHost(dockerHost)  // 例如: "tcp://localhost:2375"
                    .withDockerTlsVerify(false)  // 禁用 TLS 验证
                    .build();

            // 创建 HTTP 客户端
            DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                    .dockerHost(config.getDockerHost())
                    .maxConnections(100)
                    .connectionTimeout(Duration.ofSeconds(30))
                    .responseTimeout(Duration.ofSeconds(45))
                    .build();

            // 创建 Docker 客户端
            this.dockerClient = DockerClientImpl.getInstance(config, httpClient);
            
            // 测试连接
            try {
                dockerClient.pingCmd().exec();
                logger.info("Successfully connected to Docker daemon at {}", dockerHost);
            } catch (Exception e) {
                logger.error("Failed to connect to Docker daemon at {}. Make sure Docker is running and the port is open.", dockerHost);
                throw new RuntimeException("Failed to connect to Docker daemon", e);
            }
        } catch (Exception e) {
            logger.error("Error initializing Docker client", e);
            throw new RuntimeException("Failed to initialize Docker client", e);
        }
    }

    /**
     * 从镜像创建容器
     */
    public String createContainer(String imageName, String containerName, Map<String, String> ports) {
        try {
            // 拉取镜像
            pullImage(imageName);

            // 创建端口绑定
            List<PortBinding> portBindings = new ArrayList<>();
            ports.forEach((containerPort, hostPort) -> 
                portBindings.add(PortBinding.parse(hostPort + ":" + containerPort)));

            // 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    .withName(containerName)
                    .withHostConfig(new HostConfig().withPortBindings(portBindings))
                    .exec();

            // 启动容器
            dockerClient.startContainerCmd(container.getId()).exec();

            logger.info("Container created and started: {}", containerName);
            return container.getId();
        } catch (Exception e) {
            logger.error("Error creating container from image: {}", imageName, e);
            throw new RuntimeException("Failed to create container", e);
        }
    }

    /**
     * 从Dockerfile创建容器并实时获取日志
     */
    public String createContainerFromDockerfileWithLogs(
            File dockerfile, 
            String containerName, 
            Map<String, String> ports,
            Consumer<String> logConsumer) {
        try {
            // 构建镜像
            String imageId = buildImageWithLogs(dockerfile, logConsumer);
            logConsumer.accept("Image built successfully: " + imageId);

            // 创建端口绑定
            List<PortBinding> portBindings = new ArrayList<>();
            ports.forEach((containerPort, hostPort) -> 
                portBindings.add(PortBinding.parse(hostPort + ":" + containerPort)));

            // 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(imageId)
                    .withName(containerName)
                    .withHostConfig(new HostConfig().withPortBindings(portBindings))
                    .exec();

            // 启动容器
            dockerClient.startContainerCmd(container.getId()).exec();
            logConsumer.accept("Container started with ID: " + container.getId());

            return container.getId();
        } catch (Exception e) {
            logger.error("Error creating container from Dockerfile", e);
            throw new RuntimeException("Failed to create container from Dockerfile: " + e.getMessage());
        }
    }

    /**
     * 构建镜像并实时获取日志
     */
    private String buildImageWithLogs(File dockerfile, Consumer<String> logConsumer) {
        try {
            String imageId = dockerClient.buildImageCmd()
                .withDockerfile(dockerfile)
                .withNoCache(true)
                .withPull(true) // 自动拉取基础镜像
                .exec(new BuildImageResultCallback() {
                    @Override
                    public void onNext(BuildResponseItem item) {
                        if (item.getStream() != null) {
                            logConsumer.accept(item.getStream().trim());
                        } else if (item.getError() != null) {
                            logConsumer.accept("ERROR: " + item.getError());
                        } else if (item.getStatus() != null) {
                            logConsumer.accept(item.getStatus());
                        }
                        super.onNext(item);
                    }
                })
                .awaitImageId();

            if (imageId == null) {
                throw new RuntimeException("Failed to build image: No image ID returned");
            }

            return imageId;
        } catch (Exception e) {
            logger.error("Error building image", e);
            throw new RuntimeException("Failed to build image: " + e.getMessage());
        }
    }

    /**
     * 拉取镜像
     */
    public void pullImage(String imageName) {
        try {
            dockerClient.pullImageCmd(imageName)
                .exec(new PullImageResultCallback())
                .awaitCompletion(5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("Error pulling image: {}", imageName, e);
            throw new RuntimeException("Failed to pull image: " + e.getMessage());
        }
    }

    /**
     * 停止容器
     */
    public void stopContainer(String containerId) {
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            logger.info("Container stopped: {}", containerId);
        } catch (Exception e) {
            logger.error("Error stopping container: {}", containerId, e);
            throw new RuntimeException("Failed to stop container", e);
        }
    }

    /**
     * 删除容器
     */
    public void removeContainer(String containerId) {
        try {
            dockerClient.removeContainerCmd(containerId)
                    .withForce(true)
                    .exec();
            logger.info("Container removed: {}", containerId);
        } catch (Exception e) {
            logger.error("Error removing container: {}", containerId, e);
            throw new RuntimeException("Failed to remove container", e);
        }
    }

    /**
     * 列出所有容器
     */
    public List<Container> listContainers(boolean showAll) {
        return dockerClient.listContainersCmd()
                .withShowAll(showAll)
                .exec();
    }

    /**
     * 列出所有镜像
     */
    public List<Image> listImages() {
        return dockerClient.listImagesCmd()
                .withShowAll(true)
                .exec();
    }

    /**
     * 获取容器日志
     */
    public String getContainerLogs(String containerId) {
        try {
            LogContainerCmd logCmd = dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withTail(500); // 获取最后500行日志

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            logCmd.exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    try {
                        outputStream.write(frame.getPayload());
                    } catch (IOException e) {
                        logger.error("Error writing log frame", e);
                    }
                }
            }).awaitCompletion();

            return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error getting container logs", e);
            throw new RuntimeException("Failed to get container logs: " + e.getMessage());
        }
    }

    /**
     * 实时流式获取容器日志
     */
    public void streamContainerLogs(String containerId, Consumer<String> logConsumer) {
        try {
            LogContainerCmd logCmd = dockerClient.logContainerCmd(containerId)
                    .withStdOut(true)
                    .withStdErr(true)
                    .withFollowStream(true)
                    .withTail(100); // 从最后100行开始

            logCmd.exec(new ResultCallback.Adapter<Frame>() {
                @Override
                public void onNext(Frame frame) {
                    String log = new String(frame.getPayload(), StandardCharsets.UTF_8);
                    logConsumer.accept(log);
                }
            });
        } catch (Exception e) {
            logger.error("Error streaming container logs", e);
            throw new RuntimeException("Failed to stream container logs: " + e.getMessage());
        }
    }

    /**
     * 检查容器状态
     */
    public boolean isContainerRunning(String containerId) {
        try {
            InspectContainerResponse container = dockerClient.inspectContainerCmd(containerId).exec();
            return container.getState().getRunning();
        } catch (Exception e) {
            logger.error("Error checking container status: {}", containerId, e);
            return false;
        }
    }

    /**
     * 重启容器
     */
    public void restartContainer(String containerId) {
        try {
            dockerClient.restartContainerCmd(containerId).exec();
            logger.info("Container restarted: {}", containerId);
        } catch (Exception e) {
            logger.error("Error restarting container: {}", containerId, e);
            throw new RuntimeException("Failed to restart container", e);
        }
    }

    /**
     * 关闭 Docker 客户端
     */
    public void close() {
        try {
            if (dockerClient != null) {
                dockerClient.close();
            }
        } catch (Exception e) {
            logger.error("Error closing Docker client", e);
        }
    }

    /**
     * 启动容器
     */
    public void startContainer(String containerId) {
        try {
            dockerClient.startContainerCmd(containerId).exec();
            logger.info("Container started: {}", containerId);
        } catch (Exception e) {
            logger.error("Error starting container: {}", containerId, e);
            throw new RuntimeException("Failed to start container", e);
        }
    }

    /**
     * 添加新方法，支持拉取镜像时的实时日志
     */
    public void pullImageWithLogs(String imageName, Consumer<String> logConsumer) {
        try {
            dockerClient.pullImageCmd(imageName)
                .exec(new ResultCallback.Adapter<PullResponseItem>() {
                    @Override
                    public void onNext(PullResponseItem item) {
                        if (item.getStatus() != null) {
                            String progress = item.getProgress() != null ? item.getProgress() : "";
                            logConsumer.accept(item.getStatus() + " " + progress);
                        }
                    }
                })
                .awaitCompletion(TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new RuntimeException("Image pull interrupted", e);
        }
    }

    /**
     * 修改创建容器方法，添加日志支持
     */
    public String createContainerWithLogs(String imageName, String containerName, 
                                        Map<String, String> ports, Consumer<String> logConsumer) {
        try {
            // 检查镜像是否存在
            boolean imageExists = dockerClient.listImagesCmd()
                .exec().stream()
                .flatMap(img -> img.getRepoTags() != null ? 
                        Arrays.stream(img.getRepoTags()) : 
                        Stream.empty())
                .anyMatch(tag -> tag.equals(imageName));

            // 如果镜像不存在，则拉取
            if (!imageExists) {
                logConsumer.accept("Image not found locally, pulling...");
                pullImageWithLogs(imageName, logConsumer);
            }

            logConsumer.accept("Creating container...");
            
            // 创建端口绑定
            List<PortBinding> portBindings = new ArrayList<>();
            ports.forEach((containerPort, hostPort) -> {
                logConsumer.accept("Mapping port " + hostPort + " -> " + containerPort);
                portBindings.add(PortBinding.parse(hostPort + ":" + containerPort));
            });

            // 创建容器
            CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                    .withName(containerName)
                    .withHostConfig(new HostConfig().withPortBindings(portBindings))
                    .exec();

            logConsumer.accept("Container created with ID: " + container.getId());

            // 启动容器
            logConsumer.accept("Starting container...");
            dockerClient.startContainerCmd(container.getId()).exec();
            logConsumer.accept("Container started successfully");

            return container.getId();
        } catch (Exception e) {
            logger.error("Error creating container from image: {}", imageName, e);
            throw new RuntimeException("Failed to create container: " + e.getMessage());
        }
    }
}