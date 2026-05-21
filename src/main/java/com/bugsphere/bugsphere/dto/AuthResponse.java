package com.bugsphere.bugsphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// This is what we send BACK to the frontend after a successful login:
// { "token": "eyJhbGci...", "username": "john", "role": "ROLE_USER" }
// The frontend stores the token and sends it with every future request.
@Data
@AllArgsConstructor // generates a constructor with all 3 fields — useful for one-liner creation
public class AuthResponse {
    private String token;    // the JWT the frontend must store
    private String username; // so the frontend knows who is logged in
    private String role;     // so the frontend can show/hide admin features
}