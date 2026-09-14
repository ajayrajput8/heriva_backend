package com.madebyher.controller;

import com.madebyher.dto.AuthDtos.*;
import com.madebyher.model.User;
import com.madebyher.repository.UserRepository;
import com.madebyher.service.AuthService;
import com.madebyher.service.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUser;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PutMapping("/profile")
    public User updateProfile(
            @RequestBody ProfileUpdateRequest req) {

        User user = currentUser.get();

        if (req.fullName() != null &&
                !req.fullName().isBlank()) {

            user.setFullName(
                    req.fullName().trim()
            );
        }

        if (req.phone() != null) {
            user.setPhone(
                    req.phone().trim()
            );
        }

        return userRepository.save(user);
    }
}