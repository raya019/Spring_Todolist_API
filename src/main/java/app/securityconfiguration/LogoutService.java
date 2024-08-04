package app.securityconfiguration;

import app.entity.RefreshToken;
import app.repository.RefreshTokenRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogoutService implements LogoutHandler {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookieValue = request.getCookies()[0].getValue();
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return;
        }

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(cookieValue)
                .orElse(null);

        if (refreshToken != null) {
            response.addCookie(clearCookie());
            refreshTokenRepository.delete(refreshToken);
            SecurityContextHolder.clearContext();

            Authentication authAfterClear = SecurityContextHolder.getContext().getAuthentication();
            if (authAfterClear == null) {
                log.info("SecurityContextHolder is empty after clearing.");
            } else {
                log.warn("SecurityContextHolder is NOT empty after clearing: " + authAfterClear);
            }
        }
    }

    private Cookie clearCookie() {
        Cookie cookie = new Cookie("cookie", null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);
        return cookie;
    }
}
