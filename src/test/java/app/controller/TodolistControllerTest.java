package app.controller;

import app.entity.Todolist;
import app.entity.User;
import app.model.*;
import app.repository.RefreshTokenRepository;
import app.repository.TodolistRepository;
import app.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodolistControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodolistRepository todolistRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        todolistRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        todolistRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void addTodolist() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        userRepository.save(userRequest);

        UserLogin request = new UserLogin();
        request.setEmail(userRequest.getEmail());
        request.setPassword("password");

        WebResponse<ResponseToken> response = mapper.readValue(mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                        }
                ).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        Cookie cookie = new Cookie("cookie", response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        TodoAddRequest todoAddRequest = new TodoAddRequest();
        todoAddRequest.setTodo("Belajar Spring");
        todoAddRequest.setDescription("Belajar Spring Core");

        mockMvc.perform(
                        post("/api/todolist/add")
                                .cookie(cookie)
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(todoAddRequest)))
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            assertEquals(value.getMessage(), "Berhasil Di Tambah");
                            assertNotNull(value.getData());
                        }

                );
    }

    @Test
    void updateTodolist() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        userRepository.save(userRequest);

        UserLogin request = new UserLogin();
        request.setEmail(userRequest.getEmail());
        request.setPassword("password");

        WebResponse<ResponseToken> response = mapper.readValue(mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                        }
                ).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        Cookie cookie = new Cookie("cookie", response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        TodoAddRequest todoAddRequest = new TodoAddRequest();
        todoAddRequest.setTodo("Belajar Spring");
        todoAddRequest.setDescription("Belajar Spring Core");

        Todolist todolist = new Todolist();
        todolist.setId(UUID.randomUUID().toString());
        todolist.setTodolist("Belajar Spring");
        todolist.setDescription("Belajar Spring Core");
        todolist.setDone(false);
        todolist.setUser(userRequest);
        todolistRepository.save(todolist);

        TodoUpdateRequest todoUpdateRequest = new TodoUpdateRequest();
        todoUpdateRequest.setId(todolist.getId());
        todoUpdateRequest.setTodo("Belajar Spring Security");
        todoUpdateRequest.setDescription(todolist.getDescription());
        todoUpdateRequest.setDone(todolist.isDone());

        mockMvc.perform(
                        put("/api/todolist/update/" + todoUpdateRequest.getId())
                                .cookie(cookie)
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(todoUpdateRequest))
                )
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            assertNotNull(value.getData());
                            assertEquals("Berhasil di update",value.getMessage());
                        }
                );
    }

    @Test
    void deleteTodolist() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        userRepository.save(userRequest);

        UserLogin request = new UserLogin();
        request.setEmail(userRequest.getEmail());
        request.setPassword("password");

        WebResponse<ResponseToken> response = mapper.readValue(mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                        }
                ).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        Cookie cookie = new Cookie("cookie", response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        Todolist todolist = new Todolist();
        todolist.setId(UUID.randomUUID().toString());
        todolist.setTodolist("Belajar Spring");
        todolist.setDescription("Belajar Spring Core");
        todolist.setDone(false);
        todolist.setUser(userRequest);

        todolistRepository.save(todolist);

        mockMvc.perform(
                        delete("/api/todolist/delete/" + todolist.getId())
                                .cookie(cookie)
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<String> value = mapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            assertEquals(value.getMessage(),"Berhasil Di Hapus");
                        }
                );
    }

    @Test
    void getTodolist() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        userRepository.save(userRequest);

        UserLogin request = new UserLogin();
        request.setEmail(userRequest.getEmail());
        request.setPassword("password");

        WebResponse<ResponseToken> response = mapper.readValue(mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                ).andDo(
                        result -> {
                            WebResponse<TodoResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                        }
                ).andReturn().getResponse().getContentAsString(), new TypeReference<>() {
        });

        Cookie cookie = new Cookie("cookie", response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        //create todolist
        Todolist todolist = new Todolist();
        todolist.setId(UUID.randomUUID().toString());
        todolist.setTodolist("Belajar Spring");
        todolist.setDescription("Belajar Spring Core");
        todolist.setUser(userRequest);
        todolist.setDone(false);

        todolistRepository.save(todolist);

        Todolist todolist1 = new Todolist();
        todolist1.setId(UUID.randomUUID().toString());
        todolist1.setTodolist("Belajar Spring MVC");
        todolist1.setDescription("Belajar Authentication");
        todolist1.setDone(false);
        todolist1.setUser(userRequest);

        todolistRepository.save(todolist1);

        Todolist todolist2 = new Todolist();
        todolist2.setId(UUID.randomUUID().toString());
        todolist2.setTodolist("Belajar Spring Security");
        todolist2.setDescription("Belajar Spring Authentication");
        todolist2.setDone(false);
        todolist2.setUser(userRequest);

        todolistRepository.save(todolist2);

        Todolist todolist3 = new Todolist();
        todolist3.setId(UUID.randomUUID().toString());
        todolist3.setTodolist("Belajar Spring Web");
        todolist3.setDescription("Belajar Spring MvC");
        todolist3.setDone(false);
        todolist3.setUser(userRequest);

        todolistRepository.save(todolist3);

        Todolist todolist4 = new Todolist();
        todolist4.setId(UUID.randomUUID().toString());
        todolist4.setTodolist("Belajar Spring JPA");
        todolist4.setDescription("Belajar Spring Repository");
        todolist4.setDone(false);
        todolist4.setUser(userRequest);

        todolistRepository.save(todolist4);


        mockMvc.perform(
                        get("/api/todolist/get" )
                                .cookie(cookie)
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                )
                .andExpectAll(
                        status().isOk())
                .andDo(
                        result -> {
                            WebResponse<List<TodoResponse>> value = mapper.readValue(
                                    result.getResponse().getContentAsString(),
                                    new TypeReference<>() {
                                    });
                            assertNotNull(value.getData());
                            List.of(value.getData()).forEach(System.out::println);
                        }
                );
    }
}