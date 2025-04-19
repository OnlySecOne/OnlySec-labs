package com.only.www.onlyseclabs.controller;
import com.only.www.onlyseclabs.entity.User;
import com.only.www.onlyseclabs.service.UserService;
import com.only.www.onlyseclabs.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    // 显示用户页面
    @GetMapping("/list")
    public String showUsers(@RequestParam(required = false) String keyword, 
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        List<User> users;
        String errorMessage = null;
        try {
            if (StringUtils.hasText(keyword)) {
                users = userService.searchUsers(keyword);
            } else {
                users = userService.findAllUsers();
            }
        } catch (Exception e) {
            users = List.of(); // 空列表
            errorMessage = e.getMessage();
        }
        
        // 添加必要的分页参数
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("page", page);
        model.addAttribute("totalPages", Math.max(1, (users.size() + 9) / 10));
        model.addAttribute("sqlError", errorMessage); // 添加错误信息到模型
        return "web_vul_basic/user";
    }

    // 添加新用户
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> saveUser(@RequestBody UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setStatus(request.getStatus());
        user.setCreateTime(String.valueOf(LocalDateTime.now()));

        try {
            userService.insertUser(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 根据id删除用户
    @DeleteMapping("/id/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        System.out.println(id);
        try {
            int result = userService.deleteUser(id);
            if (result > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("删除用户失败");
        }
    }

    // 更新用户
    @PutMapping("/id/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody User user) {
        try {
            user.setId(id);
            user.setCreateTime(String.valueOf(LocalDateTime.now()));
            int result = userService.updateUser(user);
            if (result > 0) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 根据id查询单个用户
    @GetMapping("/id/{id}")
    @ResponseBody
    public ResponseEntity<?> getUser(@PathVariable String id) {
        try {
            User user = userService.findById(id);
            if (user != null) {
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("SQL错误: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<?> searchUser(@RequestParam String keyword) {
        try {
            List<User> users = userService.searchUsers(keyword);
            if (users != null && !users.isEmpty()) {
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("SQL错误: " + e.getMessage());
        }
    }

    // 用户请求对象
    public static class UserRequest {
        private String username;
        private String name;
        private String email;
        private String phone;
        private Integer status;

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "UserRequest{" +
                    "username='" + username + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}