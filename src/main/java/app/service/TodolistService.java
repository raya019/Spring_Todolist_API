package app.service;

import app.entity.Todolist;
import app.entity.User;
import app.model.TodoAddRequest;
import app.model.TodoGetResponse;
import app.model.TodoResponse;
import app.model.TodoUpdateRequest;

import java.util.List;

public interface TodolistService {
    TodoResponse addTodolist(User user, TodoAddRequest todolist);

    TodoResponse updateTodolist(User user,TodoUpdateRequest todoUpdateRequest);

    void deleteTodolist(User user,String id);

    List<TodoResponse> getAllByUser(User userEmail);
}
