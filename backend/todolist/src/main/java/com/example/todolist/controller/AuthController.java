package com.example.todolist.controller;

import com.example.todolist.dto.ApiResponse;
import com.example.todolist.dto.UserRegisterRequest;
import com.example.todolist.dto.UserLoginRequest;  // 修改导入语句
import com.example.todolist.dto.UserLoginResponse;
import com.example.todolist.model.User;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.todolist.utils.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;  // 添加 JwtUtils 依赖注入

    @PostMapping("/register")
    public ApiResponse<?> register(@RequestBody UserRegisterRequest request) {
        try {
            User user = userService.register(request);
            return ApiResponse.success("注册成功", 
                java.util.Map.of(
                    "userId", user.getId(),
                    "username", user.getUsername(),
                    "email", user.getEmail()
                ));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "注册失败：" + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@RequestBody UserLoginRequest request) {
        try {
            User user = userService.getUserByEmail(request.getEmail());
            
            if (!userService.verifyPassword(request.getPassword(), user.getPassword())) {
                return ApiResponse.error(401, "邮箱或密码错误");
            }
            
            String token = jwtUtils.generateToken(user.getId(), user.getUsername());
            
            UserLoginResponse response = new UserLoginResponse();
            response.setToken(token);
            response.setUserId(user.getId().toString());
            response.setUsername(user.getUsername());
            
            return ApiResponse.success("登录成功", response);
        } catch (Exception e) {
            return ApiResponse.error(401, "邮箱或密码错误");
        }
    }


}