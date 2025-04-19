package com.only.www.onlyseclabs.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清除session中的用户信息
        session.invalidate();
        // 重定向到登录页面
        return "redirect:/login";
    }
} 