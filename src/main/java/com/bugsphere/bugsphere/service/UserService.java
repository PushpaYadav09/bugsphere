package com.bugsphere.bugsphere.service;

import com.bugsphere.bugsphere.dto.UserResponse;
import com.bugsphere.bugsphere.entity.Role;
import com.bugsphere.bugsphere.entity.User;
import com.bugsphere.bugsphere.exception.ResourceNotFoundException;
import com.bugsphere.bugsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Get all users — admin only (enforced at controller level with @PreAuthorize)
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // Get a single user by id
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));
        return UserResponse.fromEntity(user);
    }

    // Promote a user to admin — admin only
    @Transactional
    public UserResponse makeAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));
        user.setRole(Role.ROLE_ADMIN); // change their role
        User updated = userRepository.save(user);
        return UserResponse.fromEntity(updated);
    }

    // Delete a user account — admin only
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}