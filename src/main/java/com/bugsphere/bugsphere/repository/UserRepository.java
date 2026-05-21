package com.bugsphere.bugsphere.repository;

import com.bugsphere.bugsphere.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository marks this as a Spring-managed DAO (Data Access Object).
// JpaRepository<User, Long> means:
//   - User  = the entity this repo manages
//   - Long  = the type of User's primary key (id)
// Spring automatically provides: save(), findById(), findAll(), delete(), count(), etc.
// We don't write any SQL — Spring generates it from method names!
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring reads this method name and generates:
    // SELECT * FROM users WHERE username = ?
    // Optional<> means: either a User is found, or nothing (won't throw NullPointerException)
    Optional<User> findByUsername(String username);

    // SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // SELECT COUNT(*) > 0 FROM users WHERE username = ?
    // Returns true if a user with that username already exists
    boolean existsByUsername(String username);

    // SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}