package com.only.www.onlyseclabs.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;

@Controller
public class IndexController {

    @GetMapping("/index")
    public String index(Model model, HttpSession session) {
        // 从 session 获取用户名
        String username = (String) session.getAttribute("username");

        // 如果未登录，重定向到登录页
        if (username == null) {
            return "redirect:/login";
        }

        // 将用户名传递给视图
        model.addAttribute("username", username);
        return "index";  // 这里会查找 templates/index.html
    }
}
