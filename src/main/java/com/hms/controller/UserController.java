package com.hms.controller;

import com.hms.dto.TokenRefreshResponse;
import com.hms.dto.UserDTO;
import com.hms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        String responseMessage = userService.registerUser(userDTO);
        return ResponseEntity.ok(responseMessage);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenRefreshResponse> loginUser(@RequestBody UserDTO userDTO) {
        TokenRefreshResponse token = userService.authenticateUser(userDTO.getUsername(), userDTO.getPassword());
        return ResponseEntity.ok(token);
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        UserDTO userDTO = userService.getUserByUsername(userName);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshToken(@RequestBody String requestToken) {
        return ResponseEntity.ok(userService.refreshToken(requestToken));
    }
}
