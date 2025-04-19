package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class LoginController {

    @Autowired
    private AdminServiceImpl adminService;

    @GetMapping({"/", "/login"})
    public String login(HttpSession session) {
        if (session.getAttribute("username") != null) {
            return "redirect:/index";
        }
        return "login";
    }

    @PostMapping("/login")
    public void doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            HttpServletResponse response) throws IOException {

        try {
            Admin admin = adminService.getUserByLogin(username, password);
            if (admin != null) {
                session.setAttribute("username", admin.getUsername());
                session.setAttribute("admin", admin);
                response.sendRedirect("/index");
            } else {
                response.sendRedirect("/login");
            }
        } catch (Exception e) {
            response.sendRedirect("/login");
        }
    }
}