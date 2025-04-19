package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

@Controller
@RequestMapping("/api")
public class SignLoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(SignLoginController.class);
    private static final String SECRET_KEY = "abc123456";
    private static final long TIMESTAMP_VALIDITY_PERIOD = 300;
    private final ConcurrentHashMap<String, Long> usedNonces = new ConcurrentHashMap<>();

    @Autowired
    private AdminServiceImpl adminService;

    // 模拟账号密码（明文）
    private static final String MOCK_USERNAME = "admin";
    private static final String MOCK_PASSWORD = "123456";

    @GetMapping("/secure-login")
    public String showLoginPage() {
        return "web-Decryption/sign";
    }

    @PostMapping("/secure-login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> request,
                                 @RequestHeader("X-Timestamp") String timestamp,
                                 @RequestHeader("X-Nonce") String nonce,
                                 @RequestHeader("X-Signature") String signature,
                                 HttpSession session) {
        
        // 记录接收到的参数
        logger.info("Received request parameters: {}", request);
        logger.info("Timestamp: {}, Nonce: {}, Signature: {}", timestamp, nonce, signature);

        // 计算服务器端签名
        String calculatedSignature = calculateSignature(request, timestamp, nonce);
        logger.info("Calculated signature: {}", calculatedSignature);

        // 比较签名
        if (!signature.equals(calculatedSignature)) {
            logger.error("Signature verification failed. Expected: {}, Got: {}", calculatedSignature, signature);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "签名验证失败"
            ));
        }

        // 1. 验证时间戳
        long currentTime = System.currentTimeMillis() / 1000;
        long requestTime = Long.parseLong(timestamp);
        if (Math.abs(currentTime - requestTime) > TIMESTAMP_VALIDITY_PERIOD) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "请求已过期"
            ));
        }

        // 2. 验证防重放
        if (usedNonces.containsKey(nonce)) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "重复的请求"
            ));
        }
        usedNonces.put(nonce, currentTime);

        // 替换数据库验证部分
        String username = request.get("username");
        String password = request.get("password");
        logger.info("Login attempt - username: {}, password: {}", username, password);

        // 直接使用明文密码比对
        if (MOCK_USERNAME.equals(username) && MOCK_PASSWORD.equals(password)) {
            session.setAttribute("username", username);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "登录成功！欢迎回来，" + username
            ));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "用户名或密码错误，请重试"
        ));
    }

    private String calculateSignature(Map<String, String> params, String timestamp, String nonce) {
        try {
            // 构建签名字符串
            StringBuilder signatureStr = new StringBuilder();
            
            // 添加用户名和密码参数
            signatureStr.append("username=").append(params.get("username")).append("&");
            signatureStr.append("password=").append(params.get("password")).append("&");
            signatureStr.append("timestamp=").append(timestamp).append("&");
            signatureStr.append("nonce=").append(nonce);

            String stringToSign = signatureStr.toString();
            logger.info("String to sign: {}", stringToSign);

            // 计算HMAC
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] hash = sha256_HMAC.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            // 将字节数转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            logger.error("Error calculating signature", e);
            throw new RuntimeException("签名计算失败", e);
        }
    }

    // 定期清理过期的nonce
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    public void cleanupNonces() {
        long currentTime = System.currentTimeMillis() / 1000;
        usedNonces.entrySet().removeIf(entry ->
                currentTime - entry.getValue() > TIMESTAMP_VALIDITY_PERIOD);
    }
}

