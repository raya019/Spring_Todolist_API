package app.controller;

import app.entity.RefreshToken;
import app.entity.User;
import app.model.*;
import app.repository.RefreshTokenRepository;
import app.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
class AuthControllerTest {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RefreshTokenRepository refreshToken;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        refreshToken.deleteAll();
        repository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        refreshToken.deleteAll();
        repository.deleteAll();
    }

    @Test
    void registerFailed() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName(" ");
        request.setEmail(" ");
        request.setPassword(" ");
        request.setPhone(" ");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isBadRequest()
                )
                .andDo(
                        result -> {
                            WebResponse<String> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(value.getErrors());
                            System.out.println(value.getErrors());
                        }
                );
    }

    @Test
    void registerSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test");
        request.setEmail("test@gmail.com");
        request.setPassword("password");
        request.setPhone("081234567890");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                )
                .andDo(
                        result -> {
                            WebResponse<String> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertEquals("Success Register", value.getMessage());
                        }
                );
    }

    @Test
    void LoginFailed() throws Exception {
        UserLogin request = new UserLogin();

        mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isBadRequest()
                )
                .andDo(
                        result -> {
                            WebResponse<String> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(value.getErrors());
                            System.out.println(value.getErrors());
                        }
                );
    }

    @Test
    void LoginSuccess() throws Exception {
        User userRequest = new User();
        userRequest.setName("Test");
        userRequest.setEmail("test@gmail.com");
        userRequest.setPassword(passwordEncoder.encode("password"));
        userRequest.setPhone("081234567890");

        User save = repository.save(userRequest);

        log.info("{}", save);

        UserLogin request = new UserLogin();
        request.setEmail(userRequest.getEmail());
        request.setPassword("password");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request))
                )
                .andExpectAll(
                        status().isOk()
                )
                .andDo(
                        result -> {
                            WebResponse<ResponseToken> value = mapper.readValue(
                                    result.getResponse().getContentAsString(), new TypeReference<>() {
                                    }
                            );
                            assertNotNull(result.getResponse().getCookies()[0].getValue());
                            assertEquals("Berhasil Login", value.getMessage());
                            assertNotNull(value.getData());
                            System.out.println(value.getData());
                        }
                );

    }

}