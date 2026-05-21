package com.bugsphere.bugsphere.config;

import com.bugsphere.bugsphere.security.CustomUserDetailsService;
import com.bugsphere.bugsphere.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;         // @Bean = Spring manages this object
import org.springframework.context.annotation.Configuration; // marks this as a config class
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// ↑ an AuthenticationProvider that loads users from a DB via UserDetailsService
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
// ↑ enables @PreAuthorize on controller methods (used in Phase 5)
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
// ↑ STATELESS = no sessions stored on server — every request must send a JWT
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// ↑ we insert our JWT filter BEFORE this default filter
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration       // tells Spring: this class contains @Bean definitions
@EnableWebSecurity   // activates Spring Security's web support
@EnableMethodSecurity // enables @PreAuthorize("hasRole('ADMIN')") on individual methods
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    // Defines the main security rules for HTTP requests
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // CSRF protection is for browser-session-based auth.
                // We use stateless JWT so CSRF is not needed and would break our API.
                .csrf(csrf -> csrf.disable())

                // Set up CORS — allows our React frontend (localhost:5173) to call the API
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Define which endpoints are public and which require authentication
                .authorizeHttpRequests(auth -> auth
                        // These endpoints are PUBLIC — no token needed:
                        .requestMatchers("/api/auth/**").permitAll()  // login and register
                        .requestMatchers("/api/public/**").permitAll() // any public info

                        // Everything else requires a valid JWT token:
                        .anyRequest().authenticated()
                )

                // Set session management to STATELESS.
                // This means Spring Security will NEVER create an HTTP session.
                // Every request must prove who it is via JWT — no "remember me" sessions.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Register our custom user details service and password encoder
                .authenticationProvider(authenticationProvider())

                // Add our JWT filter to run BEFORE Spring's default username/password filter.
                // Order matters: our filter reads the token → sets the user in context → then the
                // default filter runs but sees the context is already set and skips its work.
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // PasswordEncoder: BCrypt is the industry standard for hashing passwords.
    // It automatically adds a random "salt" so two identical passwords hash differently.
    // NEVER store plain-text passwords. BCrypt makes rainbow table attacks useless.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationProvider: Spring Security's component that actually verifies credentials.
    // DaoAuthenticationProvider uses our UserDetailsService to load users from DB,
    // then uses our PasswordEncoder to compare the submitted password with the stored hash.
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService); // ✅ FIX

        authProvider.setPasswordEncoder(passwordEncoder());   // how to verify the password
        return authProvider;
    }

    // AuthenticationManager is used by our AuthController to trigger the login check.
    // We just expose the default one Spring Boot already configures.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig
    ) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // CORS configuration: allows our React frontend to call the Spring Boot API.
    // Without this, the browser blocks the request because it's a different origin (port 5173 vs 8080).
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // React dev server (Vite)
                "http://localhost:3000"   // fallback if using Create React App
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*")); // allow all headers including Authorization
        config.setAllowCredentials(true);        // allow cookies and auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // apply to all endpoints
        return source;
    }
}