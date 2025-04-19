package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.utils.FileReadUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/file")
public class FileReadController {
    
    /**
     * 返回文件读取测试页面
     */
    @GetMapping("/web")
    public String fileReadPage() {
        return "web_vul_basic/file_read";
    }

    /**
     * 读取文件内容并返回为字符串
     * @param filePath 文件路径
     * @return 文件内容
     */
    @GetMapping("/filename")
    @ResponseBody
    public ResponseEntity<String> getFileContent(@RequestParam("filePath") String filePath) {
        try {
            String content = FileReadUtil.readFileAsString(filePath);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading file: " + e.getMessage());
        }
    }
}
