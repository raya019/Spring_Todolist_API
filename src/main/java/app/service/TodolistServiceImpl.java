package app.service;

import java.util.List;
import java.util.UUID;

import app.entity.Todolist;
import app.entity.User;
import app.model.TodoAddRequest;
import app.model.TodoResponse;
import app.model.TodoUpdateRequest;
import app.repository.TodolistRepository;
import app.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class TodolistServiceImpl implements TodolistService {
    @Autowired
    private TodolistRepository repository;

    @Autowired
    private ValidationUtil validationUtil;

    @Override
    @Transactional
    public TodoResponse addTodolist(User user, TodoAddRequest todolist) {
        validationUtil.validate(todolist); // validation

        // check todolist is already or not in repository
        if (repository.existsByTodolist(todolist.getTodo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Todolist sudah ada");
        }

        Todolist todo = new Todolist(); // create a new Todolist
        todo.setId(UUID.randomUUID().toString());
        todo.setTodolist(todolist.getTodo());
        todo.setDescription(todolist.getDescription());
        todo.setDone(false);
        todo.setUser(user);

        repository.save(todo); // save todolist

        return todoResponse(todo);
    }

    @Override
    @Transactional
    public TodoResponse updateTodolist(User user,TodoUpdateRequest todoUpdateRequest) {

        validationUtil.validate(todoUpdateRequest); // validation

        Todolist todolist = repository.findByUserAndId(user, todoUpdateRequest.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Todolist Tidak Ditemukan"
                ));


        Todolist newTodolist = new Todolist();
        newTodolist.setId(todoUpdateRequest.getId());
        newTodolist.setTodolist(todoUpdateRequest.getTodo());
        newTodolist.setDescription(todoUpdateRequest.getDescription());
        newTodolist.setDone(todoUpdateRequest.isDone());
        newTodolist.setUser(user);

        repository.save(newTodolist); // save todolist

        return todoResponse(newTodolist);
    }

    @Override
    @Transactional
    public void deleteTodolist(User user,String id) {
        Todolist todolist = repository.findByUserAndId(user,id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todolist Tidak Ditemukan"));

        repository.delete(todolist); // delete todolist
    }

    @Override
    @Transactional(readOnly = true)
    public List<TodoResponse> getAllByUser(User userEmail) {
        List<Todolist> todolist = repository.findAllByUser(userEmail);
        return todolist.stream().map(this::todoResponse).toList();
    }

    private TodoResponse todoResponse(Todolist todo) {
        return TodoResponse.builder()
                .id(todo.getId())
                .todo(todo.getTodolist())
                .description(todo.getDescription())
                .isDone(todo.isDone())
                .build();
    }
}
