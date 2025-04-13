package com.sd.tennis.repository;

import com.sd.tennis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<List<User>> findByRole(String role);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
