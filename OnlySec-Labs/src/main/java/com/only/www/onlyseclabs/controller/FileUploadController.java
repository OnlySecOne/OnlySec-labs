package com.only.www.onlyseclabs.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

    /**
     * 返回文件上传页面
     */
    @GetMapping("")
    public String showUploadPage() {
        return "web_vul_basic/upload";
    }

    /**
     * 漏洞1：不安全的文件上传，未检查文件类型
     */
    @PostMapping("/vuln1")
    @ResponseBody
    public String uploadVuln1(@RequestParam("file") MultipartFile file) {
        return saveFile(file, "vuln1");
    }

    /**
     * 漏洞2：仅检查文件扩展名
     */
    @PostMapping("/vuln2")
    @ResponseBody
    public String uploadVuln2(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null && filename.endsWith(".jpg")) {
            return saveFile(file, "vuln2");
        }
        return "上传失败：仅允许上传.jpg文件";
    }

    /**
     * 漏洞3：仅检查Content-Type
     */
    @PostMapping("/vuln3")
    @ResponseBody
    public String uploadVuln3(@RequestParam("file") MultipartFile file) {
        String contentType = file.getContentType();
        if ("image/jpeg".equals(contentType)) {
            return saveFile(file, "vuln3");
        }
        return "上传失败：仅允许上传JPEG图片";
    }

    /**
     * 漏洞4：删除不合法后缀
     */
    @PostMapping("/vuln4")
    @ResponseBody
    public String uploadVuln4(@RequestParam("file") MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename != null) {
            // 删除不合法后缀
            filename = filename.replaceAll("(?i)jsp", "");
            return saveFile(file, "vuln4", filename);
        }
        return "上传失败：文件名不合法";
    }

    private String saveFile(MultipartFile file, String directory) {
        return saveFile(file, directory, file.getOriginalFilename());
    }

    private String saveFile(MultipartFile file, String directory, String filename) {
        try {
            File dest = new File("uploads/" + directory + "/" + filename);
            file.transferTo(dest);
            return "文件上传成功：" + dest.getAbsolutePath();
        } catch (IOException e) {
            return "文件上传失败：" + e.getMessage();
        }
    }
}