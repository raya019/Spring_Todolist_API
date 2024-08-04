package app.controller;

import app.model.RegisterRequest;
import app.model.ResponseToken;
import app.model.UserLogin;
import app.model.WebResponse;
import app.securityconfiguration.JwtService;
import app.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/auth/")
@Slf4j
public class AuthController {

    @Autowired
    private JwtService jwt;

    @Autowired
    private AuthService service;

    @PostMapping(path = "register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterRequest RequestRegister) {
        service.register(RequestRegister);
        return WebResponse.<String>builder().message("Success Register").build();
    }

    @PostMapping(path = "login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ResponseToken> login(@RequestBody UserLogin userLogin, HttpServletResponse httpServletResponse) {
        ResponseToken login = service.login(userLogin,httpServletResponse);
        return WebResponse.<ResponseToken>builder().data(login).message("Berhasil Login").build();
    }

    @PostMapping(path = "refresh",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ResponseToken> refreshToken(HttpServletRequest req) {
        ResponseToken responseToken = service.refreshToken(req);
        return WebResponse.<ResponseToken>builder().data(responseToken).build();
    }



}
