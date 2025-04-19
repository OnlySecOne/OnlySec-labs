package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.utils.SSRFTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/ssrf")
public class SsrfController {

    /**
     * 返回SSRF测试页面
     */
    @GetMapping("/web")
    public String showSsrfPage() {
        return "web_vul_basic/web";
    }

    /**
     * 全部回显型ssrf
     */
    @GetMapping("/1")
    @ResponseBody
    public String executeSSRF_1(@RequestParam("url") String url) {
        try {
            String response = SSRFTool.sendRequest_1(url);
            return response;
        } catch (IOException e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * 只回显状态码
     */
    @GetMapping("/2")
    @ResponseBody
    public String executeSSRF_2(@RequestParam("url") String url) {
        try {
            String response = String.valueOf(SSRFTool.sendRequest_2(url));
            return "HTTP状态码: " + response;
        } catch (IOException e) {
            return "请求失败: " + e.getMessage();
        }
    }

    /**
     * Dict协议测试
     */
    @GetMapping("/3")
    @ResponseBody
    public String executeDictSSRF(@RequestParam("url") String url) {
        try {
            String s = SSRFTool.sendDictRequest_3(url);
            return "Dict协议请求执行成功。响应内容：" + s;
        } catch (IOException e) {
            return "请求失败: " + e.getMessage();
        }
    }
}

