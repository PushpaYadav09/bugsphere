package com.bugsphere.bugsphere.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// This is what the frontend sends when logging in:
// { "username": "john", "password": "secret123" }
@Data // generates getters, setters, equals, toString
public class AuthRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}