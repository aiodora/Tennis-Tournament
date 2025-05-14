package com.sd.tennis.service;

import com.sd.tennis.dto.UserDTO;
import com.sd.tennis.exception.DuplicateException;
import com.sd.tennis.exception.InvalidPasswordException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.LoginRequest;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock UserRepository repo;
    @InjectMocks UserServiceImpl service;

    UserDTO dto;
    User user;

    @BeforeEach
    void setUp() {
        dto = new UserDTO();
        dto.setUsername("player1");
        dto.setEmail("p1@example.com");
        dto.setPassword("pass123");
        dto.setRole("PLAYER");
        user = new User(); user.setUsername("player1"); user.setPassword(new BCryptPasswordEncoder().encode("pass123"));
    }

    @Test
    void registerUser_success() {
        when(repo.existsByUsername("player1")).thenReturn(false);
        when(repo.existsByEmail("p1@example.com")).thenReturn(false);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        User res = service.registerUser(dto);
        assertThat(res.getUsername()).isEqualTo("player1");
        assertThat(res.getEmail()).isEqualTo("p1@example.com");
    }

    @Test
    void registerUser_duplicateUsername_throws() {
        when(repo.existsByUsername("player1")).thenReturn(true);
        assertThatThrownBy(() -> service.registerUser(dto))
                .isInstanceOf(DuplicateException.class);
    }
}