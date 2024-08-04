package app.service;

import app.model.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    void register(RegisterRequest registerRequest);
    ResponseToken login(UserLogin userLogin,HttpServletResponse httpServletResponse);
    ResponseToken refreshToken(HttpServletRequest req) ;
}
