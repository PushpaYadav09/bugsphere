package com.bugsphere.bugsphere.dto;

import com.bugsphere.bugsphere.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

// Safe user data to return from the API — notice: NO password field!
// We never send passwords back, even hashed ones.
@Data
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().name()); // convert enum to string "ROLE_ADMIN"
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}