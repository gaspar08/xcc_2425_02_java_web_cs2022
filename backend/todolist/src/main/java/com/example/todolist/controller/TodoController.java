package com.example.todolist.controller;

import com.example.todolist.dto.*;  // Add new DTOs import
import com.example.todolist.model.Todo;
import com.example.todolist.model.User;
import com.example.todolist.service.TodoService;
import com.example.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class TodoController {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @GetMapping("/todos")
    public ApiResponse<TodoListResponse> getTodos(@RequestParam Long userId) {
        try {
            if (userId == null) {
                return ApiResponse.error(400, "用户ID不能为空");
            }

            User user = userService.getUserById(userId);
            if (user == null) {
                return ApiResponse.error(401, "用户不存在");
            }

            List<Todo> todos = todoService.getTodosByUser(user);
            List<TodoResponse> todoResponses = todos.stream()
                    .map(TodoResponse::fromTodo)
                    .collect(Collectors.toList());
            
            TodoListResponse response = new TodoListResponse();
            response.setTotal(todos.size());
            response.setItems(todoResponses);
            
            return ApiResponse.success("获取成功", response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取待办事项失败：" + e.getMessage());
        }
    }

    @PostMapping("/todos")
    public ApiResponse<TodoResponse> createTodo(@RequestBody TodoCreateRequest request, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(401, "用户不存在");
        }
    
        Todo newTodo = todoService.createTodo(request.getTitle(), user);
        return ApiResponse.success("创建成功", TodoResponse.fromTodo(newTodo));
    }

    @PutMapping("/todos/{id}")
    public ApiResponse<TodoResponse> updateTodo(@PathVariable Long id, @RequestBody TodoUpdateRequest request, @RequestParam Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ApiResponse.error(401, "用户不存在");
        }

        Todo updatedTodo = todoService.updateTodo(id, request.getTitle(), request.getCompleted(), user);
        return ApiResponse.success("更新成功", TodoResponse.fromTodo(updatedTodo));
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