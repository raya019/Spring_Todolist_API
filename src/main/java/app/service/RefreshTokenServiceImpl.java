package app.service;

import app.entity.RefreshToken;
import app.entity.User;
import app.repository.RefreshTokenRepository;
import app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${security.jwt.refresh-token}")
    private Long refreshToken;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public String generateRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiredAt(Instant.now().plusMillis(this.refreshToken));
        refreshTokenRepository.save(refreshToken);

        return refreshToken.getRefreshToken();
    }

    @Override
    @Transactional
    public Optional<RefreshToken> verifyExpiredToken(String token) {
        RefreshToken tokenDB = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token not found"));

        if (tokenDB.getExpiredAt().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(tokenDB);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        return Optional.of(tokenDB);
    }
}
