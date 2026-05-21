package com.bugsphere.bugsphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

// DTO for registration — defines exactly what the frontend sends
@Data
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be 3-30 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    // Role selected on the frontend — "ROLE_USER" or "ROLE_ADMIN"
    private String role;

    // Secret code — only required when role is ROLE_ADMIN
    // If registering as User, this can be null or empty — backend ignores it
    // If registering as Admin, this must match the code in application.properties
    private String adminCode;
}