package com.hms.service;

import com.hms.dto.TokenRefreshResponse;
import com.hms.dto.UserDTO;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import com.hms.entity.RefreshToken;
import com.hms.entity.User;
import com.hms.mapper.DoctorMapper;
import com.hms.mapper.PatientMapper;
import com.hms.repository.UserRepository;
import com.hms.security.JwtUtil;
import com.hms.security.Role;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Setter
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public String registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        User user = User.builder()
                .username(userDTO.getUsername())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .build();

        if(userDTO.getPatientDTO() != null) {
            user.setRole(Role.PATIENT);
            Patient patient = patientMapper.toPatient(userDTO.getPatientDTO());
            patient.setUser(user);
            user.setPatient(patient);

        }
        if (userDTO.getDoctorDTO() != null) {
            user.setRole(Role.DOCTOR);
            Doctor doctor = doctorMapper.toDoctor(userDTO.getDoctorDTO());
            doctor.setUser(user);
            user.setDoctor(doctor);

        }
        User savedUser = userRepository.save(user);

        if(userDTO.getPatientDTO() != null) return "Patient registered successfully";
        return "Doctor registered successfully";
    }

    public TokenRefreshResponse authenticateUser(String username, String password) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            User user = userRepository.findByUsername(username);
            if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid username or password");
            }
            String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            String refreshToken = refreshTokenService.createRefreshToken(user.getUserId());

            return new TokenRefreshResponse(jwt, refreshToken);
        }catch(Exception e) {
            throw new IllegalArgumentException("Invalid username or password");
        }

    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String userName) {
        User user = userRepository.findByUsername(userName);
        if(user == null) {
            throw new IllegalArgumentException("User not found with username: " + userName);
        }
        UserDTO userDTO = UserDTO.builder()
                .username(user.getUsername())
                .role(String.valueOf(user.getRole()))
                .build();
        if(user.getRole() == Role.PATIENT && user.getPatient() != null) {
            userDTO.setPatientDTO(patientMapper.toPatientDTO(user.getPatient()));
        } else if (user.getRole() == Role.DOCTOR && user.getDoctor() != null) {
            userDTO.setDoctorDTO(doctorMapper.toDoctorDTO(user.getDoctor()));
        }
        return userDTO;
    }

    @Transactional
    public TokenRefreshResponse refreshToken(String requestToken) {

        RefreshToken refreshToken = refreshTokenService.findByToken(requestToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        // verify not expired
        refreshTokenService.verifyExpiration(refreshToken);

        // rotate: delete old and create a new one
        Long userId = refreshToken.getUser().getUserId();
        refreshTokenService.deleteByUserId(userId);
        String newRefresh = refreshTokenService.createRefreshToken(userId);

        // issue new access token
        User user = refreshToken.getUser();
        String newAccessToken = jwtUtil.generateToken(user.getUsername(), user.getRole().name());

        return new TokenRefreshResponse(newAccessToken, newRefresh);
    }
}
