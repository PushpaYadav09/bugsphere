package com.bugsphere.bugsphere.security;

import com.bugsphere.bugsphere.repository.UserRepository;
import lombok.RequiredArgsConstructor; // Lombok: generates a constructor for all final fields
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// @Service marks this as a business-logic Spring bean (similar to @Component but more specific)
// @RequiredArgsConstructor generates: public CustomUserDetailsService(UserRepository userRepository) {}
// Spring then auto-injects UserRepository when it creates this class
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    // "final" + @RequiredArgsConstructor = constructor injection (best practice over @Autowired)

    // Spring Security calls this method automatically during login.
    // It looks up the user by username and returns their details.
    // If not found, Spring Security shows a "bad credentials" error.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                // If no user found with that username, throw this exception.
                // Spring Security catches it and returns 401 Unauthorized.
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with username: " + username
                ));
        // Our User entity implements UserDetails, so we can return it directly!
        // No conversion needed — that's the benefit of implementing UserDetails in User.java
    }
}