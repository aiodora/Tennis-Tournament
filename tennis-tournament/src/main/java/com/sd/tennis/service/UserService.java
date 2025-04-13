package com.sd.tennis.service;

import com.sd.tennis.dto.UserDTO;
import com.sd.tennis.model.LoginRequest;
import com.sd.tennis.model.User;

import java.util.List;

public interface UserService {
    User registerUser(UserDTO userDTO);
    User updateUser(Integer userId, UserDTO userDTO);
    User getUserById(Integer userId);
    void deleteUser(Integer userId);
    List<User> getAllUsers();
    List<User> getUsersByRole(String role);
    void authenticateUser(LoginRequest lr);
    User createUser(UserDTO userDTO);
}
