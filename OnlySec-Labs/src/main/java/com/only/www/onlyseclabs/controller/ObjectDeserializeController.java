package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.utils.ObjectDeserializeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller  // 改为@Controller
@RequestMapping("/deserialize")
public class ObjectDeserializeController {

    @GetMapping("/web")
    public String deserializePage() {
        return "web_vul_basic/deserialize";  // 返回deserialize.html模板
    }

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Object> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            Object obj = ObjectDeserializeUtil.deserializeFromBytes(bytes);
            return ResponseEntity.ok(obj);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deserializing file: " + e.getMessage());
        }
    }

    @PostMapping("/fromBytes")
    @ResponseBody
    public ResponseEntity<Object> deserializeFromBytes(@RequestBody byte[] serializedBytes) {
        try {
            Object obj = ObjectDeserializeUtil.deserializeFromBytes(serializedBytes);
            return ResponseEntity.ok(obj);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deserializing object: " + e.getMessage());
        }
    }

    @GetMapping("/fromFile")
    @ResponseBody
    public ResponseEntity<Object> deserializeFromFile(@RequestParam("filePath") String filePath) {
        try {
            Object obj = ObjectDeserializeUtil.deserializeFromFile(filePath);
            return ResponseEntity.ok(obj);
        } catch (IOException | ClassNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deserializing object: " + e.getMessage());
        }
    }
}