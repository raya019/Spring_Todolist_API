package app.controller;

import app.entity.User;
import app.model.*;
import app.repository.RefreshTokenRepository;
import app.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private UserRepository repository;

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
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refreshTokenRepository.deleteAll();
        repository.deleteAll();
    }

    @Test
    void getUserSuccess() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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



        Cookie cookie = new Cookie("cookie",response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        mockMvc.perform(
                        get("/user/current")
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .cookie(cookie)
                )
                .andExpectAll(
                        status().isOk()
                )
                .andDo(
                        result -> {
                            WebResponse<UserResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(value.getData());

                        }
                );
    }

    @Test
    void getUserFailedCauseAuthorizationEqualIsNull() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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

        Cookie cookie = new Cookie("cookie",response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        mockMvc.perform(
                        get("/user/current")
                )
                .andExpectAll(
                        status().isForbidden()
                );
//                .andDo(
//                        result -> {
//                            mapper.readValue(
//                                    result.getResponse().getContentAsString(), new TypeReference<>() {
//                                    }
//                            );
////                            assertEquals("Unauthorized", value.getMessage());
//                        }
//                );
    }

    @Test
    void updateUserNameAndEmailSuccess() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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

        Cookie cookie = new Cookie("cookie",response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Test Update");
        updateUserRequest.setEmail("test@gmail.com");

        mockMvc.perform(
                        patch("/user/current")
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .cookie(cookie)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(updateUserRequest))
                )
                .andExpectAll(
                        status().isOk()
                )
                .andDo(
                        result -> {
                            WebResponse<UserResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(value.getData());
                        }
                );
    }

    @Test
    void updateUserPasswordAndPhoneSuccess() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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

        Cookie cookie = new Cookie("cookie",response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setPassword("rahasia123");
        updateUserRequest.setPhone("081232131");

        mockMvc.perform(
                        patch("/user/current")
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .cookie(cookie)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(updateUserRequest))
                )
                .andExpectAll(
                        status().isOk()
                )
                .andDo(
                        result -> {
                            WebResponse<UserResponse> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(value.getData());
                        }
                );
    }

    @Test
    void updateUserFailedCauseAuthorizationEqualIsNull() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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

        UpdateUserRequest updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setName("Test Update");
        updateUserRequest.setEmail("test@gmail.com");

        mockMvc.perform(
                        patch("/user/current")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(updateUserRequest))
                )
                .andExpectAll(
                        status().isForbidden()
                );
    }

    @Test
    void logout() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        repository.save(userRequest);

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

        Cookie cookie = new Cookie("cookie",response.getData().getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        mockMvc.perform(
                        get("/user/logout")
                                .header("Authorization", "Bearer " + response.getData().getAccessToken())
                                .cookie(cookie)
                )
                .andExpectAll(
                        status().isOk()
                );
    }
}