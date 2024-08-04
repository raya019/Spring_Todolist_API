package app.repository;

import app.entity.Todolist;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TodolistRepository extends JpaRepository<Todolist, String> {
    boolean existsByTodolist(String todolist);
    Optional<Todolist> findByUserAndId(User user, String id);
    List<Todolist> findAllByUser(User emailUser);
}
