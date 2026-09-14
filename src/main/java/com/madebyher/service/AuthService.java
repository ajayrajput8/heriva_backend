package com.madebyher.service;

import com.madebyher.dto.AuthDtos.*;
import com.madebyher.model.*;
import com.madebyher.repository.*;
import com.madebyher.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final PartnerRepository partnerRepository;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email()))
            throw new IllegalArgumentException("Email already registered");

        Role role = req.role() == null ? Role.CUSTOMER : req.role();
        if (role == Role.ADMIN)
            throw new IllegalArgumentException("Admin cannot self-register");

        User user = userRepository.save(User.builder()
                .fullName(req.fullName())
                .email(req.email().toLowerCase())
                .password(passwordEncoder.encode(req.password()))
                .phone(req.phone())
                .role(role)
                .enabled(true)
                .build());

        if (role == Role.VILLAGE_PARTNER) {
            partnerRepository.save(VillagePartner.builder()
                    .user(user)
                    .status(PartnerStatus.PENDING)
                    .build());
        }

        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());
        return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
                jwtService.generateToken(details));
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password()));

        User user = userRepository.findByEmailIgnoreCase(req.email()).orElseThrow();
        UserDetails details = userDetailsService.loadUserByUsername(user.getEmail());

        return new AuthResponse(user.getId(), user.getFullName(), user.getEmail(), user.getRole(),
                jwtService.generateToken(details));
    }
}
