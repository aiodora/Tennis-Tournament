package com.sd.tennis.controller;

import com.sd.tennis.dto.UserDTO;
import com.sd.tennis.dto.UserResponseDTO;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.mapper.UserMapper;
import com.sd.tennis.model.User;
import com.sd.tennis.service.UserService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        var dtoList = users.stream()
                .map(UserMapper::toUserResponseDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserDTO userDTO) {
        var created = userService.createUser(userDTO);
        var dto = UserMapper.toUserResponseDTO(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Integer userId) {
        var user = userService.getUserById(userId);
        var dto = UserMapper.toUserResponseDTO(user);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getLoggedInUserInfo() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var loggedInUser = (User) auth.getPrincipal();
        var dto = UserMapper.toUserResponseDTO(loggedInUser);
        return ResponseEntity.ok(dto);
    }

    @PermitAll
    @GetMapping("/players")
    public ResponseEntity<List<UserResponseDTO>> getPlayers() {
        try {
            var players = userService.getUsersByRole("PLAYER");
            var dtoList = players.stream()
                    .map(UserMapper::toUserResponseDTO)
                    .toList();
            return ResponseEntity.ok(dtoList);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PermitAll
    @GetMapping("/referees")
    public ResponseEntity<List<UserResponseDTO>> getReferees() {
        try {
            var refs = userService.getUsersByRole("REFEREE");
            var dtoList = refs.stream()
                    .map(UserMapper::toUserResponseDTO)
                    .toList();
            return ResponseEntity.ok(dtoList);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PermitAll
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Integer userId, @RequestBody UserDTO userDTO) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var loggedInUser = (User) auth.getPrincipal();
        String currentUsername = loggedInUser.getUsername();

        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));

        var targetUser = userService.getUserById(userId);
        String targetUsername = targetUser.getUsername();

        if (!isAdmin && !targetUsername.equals(currentUsername)) {
            return ResponseEntity.status(403).build();
        }

        var updated = userService.updateUser(userId, userDTO);
        var dto = UserMapper.toUserResponseDTO(updated);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted");
    }
}
