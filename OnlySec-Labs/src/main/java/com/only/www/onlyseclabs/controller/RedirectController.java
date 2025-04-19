package com.only.www.onlyseclabs.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/redirect")
public class RedirectController {

    /**
     * 漏洞代码：接受用户提供的 URL 并直接跳转
     *
     * @return 消息字符串
     */
    @GetMapping("/web")
    public String showPage(){
        return "web_vul_basic/redirect";
    }
    @GetMapping("/url")
    public void vulnerableRedirect(@RequestParam("target") String target, HttpServletResponse response) {
        try {
            // 未验证目标 URL，直接跳转
            response.sendRedirect(target);
        } catch (Exception e) {
            throw new RuntimeException("Redirection failed: " + e.getMessage());
        }
    }
}