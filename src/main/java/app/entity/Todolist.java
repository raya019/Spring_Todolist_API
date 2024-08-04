package app.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "todolist")
public class Todolist {

    @Id
    private String id;

    @Column(name = "name_todolist")
    private String todolist;

    private String description;

    private boolean isDone;

    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private User user;


}

