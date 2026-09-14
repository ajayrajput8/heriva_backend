package com.madebyher.dto;

import com.madebyher.model.Role;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class AuthDtos {
    public record RegisterRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            @Size(min=6) String password,
            String phone,
            Role role) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password) {}

    public record ProfileUpdateRequest(
            String fullName,
            String phone
    ) {}

    public record AuthResponse(
            Long userId,
            String fullName,
            String email,
            Role role,
            String token) {}
}
