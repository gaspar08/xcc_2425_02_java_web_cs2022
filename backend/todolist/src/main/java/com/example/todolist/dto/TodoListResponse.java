package com.example.todolist.dto;

import java.util.List;

public class TodoListResponse {
    private long total;
    private List<TodoResponse> items;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<TodoResponse> getItems() {
        return items;
    }

    public void setItems(List<TodoResponse> items) {
        this.items = items;
    }
}