package app.service;

import app.entity.RefreshToken;
import app.entity.User;
import app.model.RegisterRequest;
import app.model.ResponseToken;
import app.model.UserLogin;
import app.repository.RefreshTokenRepository;
import app.repository.UserRepository;
import app.securityconfiguration.JwtService;
import app.util.ValidationUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ValidationUtil validationUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public void register(RegisterRequest registerRequest) {
        validationUtil.validate(registerRequest); // validation

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email sudah ada");
        }

        User users = new User();
        users.setName(registerRequest.getName());
        users.setEmail(registerRequest.getEmail());
        users.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        users.setPhone(registerRequest.getPhone());
        userRepository.save(users);
    }

    @Override
    @Transactional
    public ResponseToken login(UserLogin userLogin,HttpServletResponse httpServletResponse) {
        validationUtil.validate(userLogin); // validation

        log.info("Login user {}", userLogin);
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLogin.getEmail(), userLogin.getPassword()));

        log.info("Login user {}", authenticate);

        String accessToken = null;
        String generatedRefreshToken = null;
        if (authenticate.isAuthenticated()) {

            User user = userRepository
                    .findByEmail(userLogin.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Email atau Password Salah"));

            if (!passwordEncoder.matches(userLogin.getPassword(), user.getPassword())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email atau Password Salah");
            }
            accessToken = jwtService.generateToken(user);

            generatedRefreshToken = refreshTokenService.generateRefreshToken(user.getEmail());

            Cookie cookie = new Cookie("cookie", generatedRefreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            httpServletResponse.addCookie(cookie);
        }

            return ResponseToken
                    .builder()
                    .accessToken(accessToken)
                    .refreshToken(generatedRefreshToken)
                    .build();
    }

    @Override
    public ResponseToken refreshToken(HttpServletRequest req) {
        String tokenValueCookie = req.getCookies()[0].getValue();
        if (tokenValueCookie == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token tidak ditemukan");
        }
        var user = refreshTokenService
                .verifyExpiredToken(tokenValueCookie)
                .stream()
                .map(RefreshToken::getUser)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User tidak ditemukan"));
        String newAccessToken = jwtService.generateToken(user);
            return ResponseToken
                    .builder()
                    .accessToken(newAccessToken)
                    .refreshToken(tokenValueCookie)
                    .build();
    }
}
