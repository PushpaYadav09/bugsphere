package com.bugsphere.bugsphere.entity;

import jakarta.persistence.*;           // JPA annotations that map this class to a DB table
import jakarta.validation.constraints.*; // annotations like @NotBlank, @Email for input validation
import lombok.AllArgsConstructor;       // Lombok: auto-generates a constructor with all fields
import lombok.Builder;                  // Lombok: lets us use User.builder().name("x").build()
import lombok.Data;                     // Lombok: auto-generates getters, setters, equals, hashCode, toString
import lombok.NoArgsConstructor;        // Lombok: auto-generates an empty constructor (required by JPA)
import org.springframework.security.core.GrantedAuthority;         // Spring Security interface for roles
import org.springframework.security.core.authority.SimpleGrantedAuthority; // concrete implementation of GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails;  // Spring Security interface — makes User work with Spring auth

import java.time.LocalDateTime;         // for storing when this user was created
import java.util.Collection;            // Java Collection, returned by getAuthorities()
import java.util.List;                  // List.of(...) used in getAuthorities()

@Data                  // generates: getters, setters, equals(), hashCode(), toString()
@Builder               // generates: User.builder().username("x").email("y").build()
@NoArgsConstructor     // generates: new User() — JPA needs this to create objects from DB rows
@AllArgsConstructor    // generates: new User(id, username, email, ...) — used by @Builder
@Entity                // tells JPA: this class is a database table
@Table(name = "users") // the table will be named "users" in PostgreSQL
public class User implements UserDetails {
    // UserDetails is a Spring Security interface.
    // By implementing it, Spring Security can use our User class
    // directly for login — no need to maintain a separate auth object.

    @Id                                                    // this is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)    // auto-increment: 1, 2, 3, ...
    private Long id;

    @NotBlank(message = "Username is required")  // validation: can't be empty or whitespace
    @Column(unique = true, nullable = false)     // DB constraint: no two users with same username
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email")    // validation: checks for proper email format
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;   // this will store the HASHED password, never plain text

    @Enumerated(EnumType.STRING)  // store the enum as a string in DB ("ROLE_ADMIN"), not a number
    @Column(nullable = false)
    private Role role;            // either ROLE_ADMIN or ROLE_USER

    @Column(updatable = false)    // once set, this can never be changed via JPA updates
    private LocalDateTime createdAt;

    @PrePersist  // this method runs automatically just BEFORE this user is saved to DB for the first time
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // auto-set the creation timestamp
    }

    // ── Methods required by UserDetails interface ──────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring Security needs a list of roles/permissions for this user.
        // We only have one role per user, so we return a single-element list.
        // SimpleGrantedAuthority wraps the role string "ROLE_ADMIN" or "ROLE_USER"
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;  // Spring Security uses this when checking login credentials
    }

    @Override
    public String getUsername() {
        return username;  // Spring Security uses this to look up the user during login
    }

    // The next 4 methods are from UserDetails — we return true for all of them.
    // They exist for advanced use cases like locking accounts or expiring passwords.
    // For BugSphere we keep it simple: all accounts are always active.

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}