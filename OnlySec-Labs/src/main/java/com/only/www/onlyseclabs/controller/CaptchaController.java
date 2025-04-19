package com.only.www.onlyseclabs.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.only.www.onlyseclabs.entity.Admin;
import com.only.www.onlyseclabs.service.impl.CaptchaServiceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private CaptchaServiceImpl captchaService;

    @GetMapping("")
    public String showCaptchaPage() {
        return "web_vul_basic/captcha";
    }

    @GetMapping("/generate")
    public void generateCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
        // 生成验证码图片
        int width = 100;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        // 生成随机验证码
        String captcha = generateRandomCode();
        
        // 将验证码存入session（不销毁）
        session.setAttribute("captcha", captcha);

        // 绘制验证码
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString(captcha, 20, 30);

        // 添加干扰线
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
            g.drawLine(random.nextInt(width), random.nextInt(height),
                    random.nextInt(width), random.nextInt(height));
        }

        // 输出图片
        response.setContentType("image/jpeg");
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }

    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String username,
                                   @RequestParam String password,
                                   @RequestParam String captcha,
                                   HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取session中的验证码（注意这里不会删除session中的验证码，形成漏洞）
        String sessionCaptcha = (String) session.getAttribute("captcha");
        log.info("验证码校验: 输入={}, Session中={}", captcha, sessionCaptcha);

        if (sessionCaptcha != null && sessionCaptcha.equalsIgnoreCase(captcha)) {
            // 验证码正确，调用service进行数据库验证
            Admin admin = captchaService.getCaptcha(username, password);
            log.info("数据库验证结果: {}", admin != null ? "成功" : "失败");
            
            if (admin != null) {
                result.put("success", true);
                result.put("message", "登录成功！");
            } else {
                result.put("success", false);
                result.put("message", "用户名或密码错误！");
            }
        } else {
            result.put("success", false);
            result.put("message", "验证码错误！");
        }

        return result;
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
} 