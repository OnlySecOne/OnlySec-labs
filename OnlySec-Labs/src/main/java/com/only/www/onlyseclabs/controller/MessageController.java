package com.only.www.onlyseclabs.controller;

import com.only.www.onlyseclabs.entity.Message;
import com.only.www.onlyseclabs.service.impl.MessageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/xss/api/messages")
public class MessageController {

    @Autowired
    private MessageServiceImpl messageService;

    // 显示留言页面
    @GetMapping("/show")
    public String showMessages(@RequestParam(required = false) String keyword, Model model) {
        List<Message> messages;
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 如果有关键词，执行搜索
            messages = messageService.searchMessages("%" + keyword + "%");
        } else {
            // 否则获取所有留言
            messages = messageService.getAllMessages();
        }
        model.addAttribute("messages", messages);
        return "web_vul_basic/message";
    }

    // 添加新留言
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addMessage(@RequestBody MessageRequest request) {
        // 添加调试日志
        System.out.println("收到的请求内容：" + request.getContent());
        
        // 确保 MessageRequest 类是 public 的且是静态的
        Message message = new Message();
        message.setContent(request.getContent());
        message.setCreateTime(LocalDateTime.now());
        message.setUserId(3);
        
        try {
            messageService.insertMessage(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace(); // 添加异常打印
            return ResponseEntity.badRequest().body("添加留言失败");
        }
    }
    //根据id删除留言
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMessage(@PathVariable int id) {
        try {
            int result = messageService.deleteMessage(id);
            if (result > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("删除留言失败");
        }
    }
    //关键字搜索留言
    @GetMapping("/search")
    public String listMessages(@RequestParam(required = false) String keyword, Model model) {
        List<Message> messages;
        if (StringUtils.hasText(keyword)) {
            // 如果有关键词，执行搜索
            messages = messageService.searchMessages(keyword);
        } else {
            // 否则获取所有消息
            messages = messageService.getAllMessages();
        }
        model.addAttribute("messages", messages);
        model.addAttribute("keyword",keyword);
        return "web_vul_basic/message";
    }
    //更新留言
    @PostMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<?> updateMessage(@PathVariable int id, @RequestBody Message message) {
        message.setId(id);
        int result = messageService.updateMessage(message);
        if (result > 0) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //根据id查询单条留言
    @GetMapping("/show/{id}")
    @ResponseBody  // 返回JSON数据
    public ResponseEntity<Message> getMessage(@PathVariable int id) {
        Message message = messageService.getMessageById(id);
        if (message != null) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // 将内部类改为 public static
    public static class MessageRequest {
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}