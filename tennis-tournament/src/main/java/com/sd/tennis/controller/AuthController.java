package com.sd.tennis.controller;

import com.sd.tennis.dto.UserDTO;
import com.sd.tennis.model.LoginRequest;
import com.sd.tennis.service.UserService;
import com.sd.tennis.util.JwtUtil;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PermitAll
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        userService.registerUser(userDTO);
        return ResponseEntity.ok("User registered successfully");
    }

    @PermitAll
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest lr) {
        //String username = lr.getUsername();
        userService.authenticateUser(lr);
        String token = jwtUtil.generateToken(lr.getUsername());
        return ResponseEntity.ok(token);
    }
}
