package app.repository;

import app.entity.RefreshToken;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    Optional<RefreshToken>findByRefreshTokenAndUser(String token, User user);

    Optional<RefreshToken> findByRefreshToken(String token);
}
