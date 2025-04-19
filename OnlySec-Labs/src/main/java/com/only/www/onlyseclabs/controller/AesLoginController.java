package com.only.www.onlyseclabs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/api")
public class AesLoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(AesLoginController.class);
    private static final String MOCK_USERNAME = "admin";
    private static final String MOCK_PASSWORD = "123456";
    private static final String AES_KEY_ATTR = "aes_key";

    @GetMapping("/aes-login")
    public String showLoginPage() {
        return "web-Decryption/aes";
    }

    @GetMapping("/aes-key")
    @ResponseBody
    public ResponseEntity<?> getAesKey(HttpSession session) {
        try {
            String existingKey = (String) session.getAttribute(AES_KEY_ATTR);
            if (existingKey != null) {
                logger.info("Using existing AES key from session: {}", session.getId());
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "key", existingKey
                ));
            }

            String newKey = UUID.randomUUID().toString().substring(0, 16);
            session.setAttribute(AES_KEY_ATTR, newKey);
            
            logger.info("Generated new AES key: {} for session: {}", newKey, session.getId());
            logger.info("Session attributes: {}", session.getAttributeNames());
            
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "key", newKey
            ));
        } catch (Exception e) {
            logger.error("Error generating AES key", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "生成密钥失败: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/aes-login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String key = (String) session.getAttribute(AES_KEY_ATTR);
        logger.info("Session ID: {}", session.getId());
        logger.info("AES key from session: {}", key);
        
        if (key == null) {
            logger.error("No AES key found in session. Session attributes: {}", session.getAttributeNames());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "未找到加密密钥，请刷新页面重试"
            ));
        }

        String encryptedUsername = request.get("username");
        String encryptedPassword = request.get("password");

        try {
            String username = aesDecrypt(encryptedUsername, key);
            String password = aesDecrypt(encryptedPassword, key);
            
            logger.info("Decrypted credentials - username: {}", username);

            if (MOCK_USERNAME.equals(username) && MOCK_PASSWORD.equals(password)) {
                session.setAttribute("username", username);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "登录成功！欢迎回来，" + username
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名或密码错误"
            ));

        } catch (Exception e) {
            logger.error("Login error - Session ID: {}", session.getId(), e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "解密失败: " + e.getMessage()
            ));
        }
    }

    private String aesDecrypt(String encryptedText, String key) throws Exception {
        try {
            // 创建密钥
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

            // 创建密码器
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            // Base64解码
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            
            // 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            // 转换为字符串
            String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);
            
            logger.info("Decryption successful - Input: {}, Output: {}", encryptedText, decryptedText);
            return decryptedText;

        } catch (Exception e) {
            logger.error("Decryption failed - Input: {}, Key: {}, Error: {}", 
                        encryptedText, key, e.getMessage());
            throw new Exception("解密失败: " + e.getMessage());
        }
    }
} 