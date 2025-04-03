package com.example.todolist.service;

import com.example.todolist.model.Todo;
import com.example.todolist.model.User;
import com.example.todolist.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;

    public List<Todo> getTodosByUser(User user) {
        return todoRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
    }

    public Todo createTodo(String content, User user) {
        Todo todo = new Todo();
        todo.setContent(content);
        todo.setUser(user);
        return todoRepository.save(todo);
    }

    public Todo updateTodo(Long id, String content, boolean completed, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        
        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todo.setContent(content);
        todo.setCompleted(completed);
        return todoRepository.save(todo);
    }

    public void deleteTodo(Long id, User user) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        if (!todo.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        todoRepository.delete(todo);
    }
}