package com.hms.service;

import com.hms.entity.Patient;
import com.hms.entity.RefreshToken;
import com.hms.entity.User;
import com.hms.repository.RefreshTokenRepository;
import com.hms.repository.UserRepository;
import com.hms.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .userId(1L)
                .username("testuser")
                .password("pass")
                .role(Role.PATIENT)
                .patient(Patient.builder().patientId(1L).firstName("Keshav").lastName("Garg")
                        .dob(LocalDate.parse("2000-09-01")).gender("Male")
                        .phone("+1234567890").email("keshav@gmail.com").build())
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiryDate(Instant.now().plus(7, ChronoUnit.DAYS))
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void testCreateRefreshToken_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        String result = refreshTokenService.createRefreshToken(1L);

        assertThat(result).isEqualTo(refreshToken.getToken());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void testVerifyToken_expired() {
        refreshToken.setExpiryDate(Instant.now().minusSeconds(60));
        when(refreshTokenRepository.findByToken(refreshToken.getToken()))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(RuntimeException.class,
                () -> refreshTokenService.verifyExpiration(refreshToken));
    }
}
