package com.hms.service;

import com.hms.entity.RefreshToken;
import com.hms.entity.User;
import com.hms.exception.TokenRefreshException;
import com.hms.repository.RefreshTokenRepository;
import com.hms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // configurable lifetime (days)
    @Value("${security.refresh-token.expiration-days:7}")
    private Long refreshTokenDurationDays;

    public String createRefreshToken(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(refreshTokenDurationDays, ChronoUnit.DAYS))
                .build();

        refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Verifies expiry — if expired, deletes it and throws TokenRefreshException.
     * Returns the same token if still valid.
     */
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            // remove expired token
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please login again.");
        }
        return token;
    }

    /**
     * Delete all refresh tokens for a given user id (used for logout).
     * Returns number of tokens deleted.
     */
    @Transactional
    public int deleteByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Optional scheduled cleanup for expired tokens (runs daily).
     * Enable scheduling by adding @EnableScheduling to main class.
     */
    @Scheduled(cron = "${security.refresh-token.cleanup-cron:0 0 3 * * ?}")
    public void removeExpiredTokens() {
        refreshTokenRepository.findAll().stream()
                .filter(t -> t.getExpiryDate().isBefore(Instant.now()))
                .forEach(refreshTokenRepository::delete);
    }
}
