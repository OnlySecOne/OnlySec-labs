package com.only.www.onlyseclabs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Hex;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/api")
public class Sm4LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(Sm4LoginController.class);
    
    // SM4密钥（16字节）
    private static final String SM4_KEY = "1234567890abcdef";
    
    // 模拟账号密码
    private static final String MOCK_USERNAME = "admin";
    private static final String MOCK_PASSWORD = "123456";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @GetMapping("/sm4-login")
    public String showLoginPage() {
        return "web-Decryption/sm4";
    }

    @PostMapping("/sm4-login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String encryptedUsername = request.get("username");
        String encryptedPassword = request.get("password");
        
        logger.info("Received encrypted credentials - username: {}, password: {}", encryptedUsername, encryptedPassword);

        try {
            // SM4解密
            String decryptedUsername = sm4Decrypt(encryptedUsername, SM4_KEY);
            String decryptedPassword = sm4Decrypt(encryptedPassword, SM4_KEY);
            
            logger.info("Decrypted credentials - username: {}, password: {}", decryptedUsername, decryptedPassword);

            // 验证用户名和密码
            if (MOCK_USERNAME.equals(decryptedUsername) && MOCK_PASSWORD.equals(decryptedPassword)) {
                session.setAttribute("username", decryptedUsername);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "登录成功！欢迎回来，" + decryptedUsername
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名或��码错误"
            ));

        } catch (Exception e) {
            logger.error("Decryption failed", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "解密失败"
            ));
        }
    }

    private String sm4Decrypt(String ciphertext, String key) throws Exception {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec sm4Key = new SecretKeySpec(keyBytes, "SM4");
        
        Cipher cipher = Cipher.getInstance("SM4/ECB/PKCS5Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, sm4Key);
        
        byte[] decrypted = cipher.doFinal(Hex.decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
} 