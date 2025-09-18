package com.hms.service;

import com.hms.dto.TokenRefreshResponse;
import com.hms.dto.UserDTO;
import com.hms.entity.*;
import com.hms.mapper.DoctorMapper;
import com.hms.mapper.PatientMapper;
import com.hms.repository.UserRepository;
import com.hms.security.JwtUtil;
import com.hms.security.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PatientMapper patientMapper;
    @Mock
    private DoctorMapper doctorMapper;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // manually set mock into userService
        userService.setAuthenticationManager(authenticationManager); // add setter in UserService
    }


    // ---- registerUser ----
    @Test
    void registerUser_shouldRegisterPatient() {
        UserDTO dto = UserDTO.builder()
                .username("john")
                .password("123")
                .patientDTO(new com.hms.dto.PatientDTO())
                .build();

        when(userRepository.existsByUsername("john")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        Patient patient = new Patient();
        when(patientMapper.toPatient(any())).thenReturn(patient);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        String result = userService.registerUser(dto);

        assertEquals("Patient registered successfully", result);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_shouldThrowIfUsernameExists() {
        UserDTO dto = UserDTO.builder().username("john").password("123").build();
        when(userRepository.existsByUsername("john")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(dto));
    }

    // ---- authenticateUser ----
    @Test
    void authenticateUser_shouldReturnTokens() {
        // User in DB
        User user = User.builder()
                .userId(1L)
                .username("john")
                .password("encoded")  // this is the "encoded" password stored
                .role(Role.PATIENT)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(user);

// Mock authenticationManager to not throw
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(invocation -> {
                    // simulate success: do nothing
                    return mock(org.springframework.security.core.Authentication.class);
                });

// Mock passwordEncoder to match input
        when(passwordEncoder.matches("123", "encoded")).thenReturn(true);

// Mock JWT and refresh token generation
        when(jwtUtil.generateToken("john", "PATIENT")).thenReturn("jwt-token");
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("refresh-token");

        TokenRefreshResponse response = userService.authenticateUser("john", "123");

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());

    }

    @Test
    void authenticateUser_shouldThrowIfInvalidPassword() {
        User user = User.builder().username("john").password("encoded").role(Role.PATIENT).build();
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.authenticateUser("john", "wrong"));
    }

    // ---- getUserByUsername ----
    @Test
    void getUserByUsername_shouldReturnUserDTO() {
        User user = User.builder().username("john").role(Role.PATIENT).patient(new Patient()).build();
        when(userRepository.findByUsername("john")).thenReturn(user);
        when(patientMapper.toPatientDTO(any())).thenReturn(new com.hms.dto.PatientDTO());

        UserDTO dto = userService.getUserByUsername("john");

        assertEquals("john", dto.getUsername());
        assertEquals("PATIENT", dto.getRole());
    }

    @Test
    void getUserByUsername_shouldThrowIfNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> userService.getUserByUsername("unknown"));
    }

    // ---- refreshToken ----
    @Test
    void refreshToken_shouldReturnNewTokens() {
        User user = User.builder().userId(1L).username("john").role(Role.PATIENT).build();
        RefreshToken oldToken = RefreshToken.builder().token("old").user(user).build();

        when(refreshTokenService.findByToken("old")).thenReturn(Optional.of(oldToken));
        when(refreshTokenService.createRefreshToken(1L)).thenReturn("new-refresh");
        when(jwtUtil.generateToken("john", "PATIENT")).thenReturn("new-jwt");

        TokenRefreshResponse response = userService.refreshToken("old");

        assertEquals("new-jwt", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
    }

    @Test
    void refreshToken_shouldThrowIfNotFound() {
        when(refreshTokenService.findByToken("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.refreshToken("invalid"));
    }
}
