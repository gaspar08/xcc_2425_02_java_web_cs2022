package com.example.todolist.service;

import com.example.todolist.dto.UserRegisterRequest;
import com.example.todolist.model.User;
import com.example.todolist.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(UserRegisterRequest request) {
        try {
            // 创建新用户
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail().trim());
            user.setPassword(passwordEncoder.encode(request.getPassword()));

            // 保存用户
            return userRepository.save(user);
        } catch (Exception e) {
            // 处理数据库唯一性约束异常
            if (e.getMessage() != null && e.getMessage().contains("UK_6DOTKOTT2KJSP8VW4D0M25FB7_INDEX_4")) {
                throw new IllegalArgumentException("邮箱已存在");
            }
            throw e;
        }
    }
}