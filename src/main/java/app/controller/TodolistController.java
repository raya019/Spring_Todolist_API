package app.controller;

import app.entity.Todolist;
import app.entity.User;
import app.model.*;
import app.service.TodolistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/todolist/")
public class TodolistController {
    private static final Logger log = LoggerFactory.getLogger(TodolistController.class);
    @Autowired
    private TodolistService service;

    @PostMapping(path = "add",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TodoResponse> addTodolist(User user, @RequestBody TodoAddRequest request) {
        TodoResponse todoResponse = service.addTodolist(user, request);
        return WebResponse.<TodoResponse>builder().data(todoResponse).message("Berhasil Di Tambah").build();
    }

    @PutMapping(
            path = "update/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TodoResponse> updateTodolist(
            User user,
            @RequestBody TodoUpdateRequest todoUpdate,
            @PathVariable("id") String idString
    ) {

        todoUpdate.setId(idString);
        log.info("{}",idString);
        log.info("{}",todoUpdate);
        TodoResponse updateTodolist = service.updateTodolist(user,todoUpdate);
        return WebResponse.<TodoResponse>builder().data(updateTodolist).message("Berhasil di update").build();
    }

    @DeleteMapping(
            path = "delete/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> deleteTodolist(User user,@PathVariable("id") String idString) {
        service.deleteTodolist(user,idString);
        return WebResponse.<String>builder().message("Berhasil Di Hapus").build();
    }

    @GetMapping(
            path = "get",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<TodoResponse>> getTodolist(User user) {
        List<TodoResponse> todolistByUser = service.getAllByUser(user);
        return WebResponse.<List<TodoResponse>>builder().data(todolistByUser).build();
    }

}
