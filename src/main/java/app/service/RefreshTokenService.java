package app.service;

import app.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    String generateRefreshToken(String token);
    Optional<RefreshToken> verifyExpiredToken(String token);
}
