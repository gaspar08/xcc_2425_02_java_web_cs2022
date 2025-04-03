package com.example.todolist.controller;

import com.example.todolist.dto.ApiResponse;
import com.example.todolist.model.Todo;
import com.example.todolist.model.User;
import com.example.todolist.service.TodoService;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TodoController {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @GetMapping("/todos")
    public ApiResponse<List<Todo>> getTodos(@RequestParam Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }

            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(401, "用户不存在");
            }

            List<Todo> todos = todoService.getTodosByUser(user);
            return ApiResponse.success(todos);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取待办事项失败：" + e.getMessage());
        }
    }

    @PostMapping("/todos")
    public ApiResponse createTodo(@RequestBody Todo todo, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(401, "用户不存在");
        }

        Todo newTodo = todoService.createTodo(todo.getContent(), user);
        return ApiResponse.success(newTodo);
    }

    @PutMapping("/todos/{id}")
    public ApiResponse updateTodo(@PathVariable Long id, @RequestBody Todo todo, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(401, "用户不存在");
        }

        Todo updatedTodo = todoService.updateTodo(id, todo.getContent(), todo.isCompleted(), user);
        return ApiResponse.success(updatedTodo);
    }

    @DeleteMapping("/todos/{id}")
    public ApiResponse deleteTodo(@PathVariable Long id, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(401, "用户不存在");
        }

        todoService.deleteTodo(id, user);
        return ApiResponse.success(null);
    }
}