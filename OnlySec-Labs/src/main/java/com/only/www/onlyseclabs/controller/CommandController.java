package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.utils.SystemCommandExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;

@Controller
@RequestMapping("/command")
public class CommandController {

    @GetMapping("/web")
    public String commandPage() {
        return "web_vul_basic/command_exec";
    }

    /**
     * 执行系统命令并返回结果
     * @param command 要执行的命令
     * @return 执行结果
     */
    @GetMapping("/exec")
    @ResponseBody
    public String executeCommand(@RequestParam("command") String command) {
        try {
            // 调用工具类执行命令并返回结果
            String result = SystemCommandExecutor.executeCommand(command);            return result;  // 返回执行结果
        } catch (Exception e) {
            return "Error executing command: " + e.getMessage();
        }
    }
}