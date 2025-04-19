package com.only.www.onlyseclabs.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/api")
public class Md5LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(Md5LoginController.class);
    
    // 模拟账号密码
    private static final String MOCK_USERNAME = "admin";
    // admin的MD5值
    private static final String MOCK_PASSWORD_MD5 = "e10adc3949ba59abbe56e057f20f883e"; // 123456的MD5值

    @GetMapping("/md5-login")
    public String showLoginPage() {
        return "web-Decryption/md5";
    }

    @PostMapping("/md5-login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody Map<String, String> request, HttpSession session) {
        String base64Username = request.get("username");
        String md5Password = request.get("password");
        
        logger.info("Received encrypted credentials - username(Base64): {}, password(MD5): {}", base64Username, md5Password);

        try {
            // Base64解码用户名
            String decodedUsername = new String(Base64.getDecoder().decode(base64Username));
            logger.info("Decoded username: {}", decodedUsername);

            // 验证用户名和密码
            if (MOCK_USERNAME.equals(decodedUsername) && MOCK_PASSWORD_MD5.equals(md5Password)) {
                session.setAttribute("username", decodedUsername);
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "登录成功！欢迎回来，" + decodedUsername
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名或密码错误"
            ));

        } catch (IllegalArgumentException e) {
            logger.error("Base64 decoding failed", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "用户名解码失败"
            ));
        }
    }
} 