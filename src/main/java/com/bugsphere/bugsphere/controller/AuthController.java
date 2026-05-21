package com.bugsphere.bugsphere.controller;

import com.bugsphere.bugsphere.dto.AuthRequest;
import com.bugsphere.bugsphere.dto.AuthResponse;
import com.bugsphere.bugsphere.dto.RegisterRequest;
import com.bugsphere.bugsphere.entity.Role;
import com.bugsphere.bugsphere.entity.User;
import com.bugsphere.bugsphere.repository.UserRepository;
import com.bugsphere.bugsphere.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Reads admin.secret.code from application.properties and injects it here
    // This keeps the secret out of the code — it lives only in config
    @Value("${admin.secret.code}")
    private String adminSecretCode;

    // ── POST /api/auth/register ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        // Check username and email uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // ── Role + security check ────────────────────────────────────────────
        Role role = Role.ROLE_USER; // safe default — everyone starts as user

        if ("ROLE_ADMIN".equals(request.getRole())) {
            // User wants to register as Admin — verify the secret code

            // Check 1: adminCode must not be null or empty
            // Check 2: adminCode must exactly match what's in application.properties
            // If either fails → reject with 403 Forbidden
            if (request.getAdminCode() == null ||
                    !adminSecretCode.equals(request.getAdminCode())) {

                // 403 Forbidden — not a validation error, a security rejection
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body("Invalid admin code");
            }

            // Code is correct — allow admin registration
            role = Role.ROLE_ADMIN;
        }

        // Build and save the user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("User registered successfully");
    }

    // ── POST /api/auth/login ─────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {

        // Verify username + password via Spring Security
        // Throws BadCredentialsException if wrong → returns 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Load user and generate token
        UserDetails userDetails = userRepository
                .findByUsername(request.getUsername())
                .orElseThrow();

        String token = jwtUtil.generateToken(userDetails);
        User user = (User) userDetails;

        return ResponseEntity.ok(
                new AuthResponse(token, user.getUsername(), user.getRole().name())
        );
    }
}