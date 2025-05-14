package com.sd.tennis.service;

import com.sd.tennis.dto.LoginDTO;
import com.sd.tennis.dto.UserDTO;
import com.sd.tennis.exception.DuplicateException;
import com.sd.tennis.exception.InvalidPasswordException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.LoginRequest;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.UserRepository;
import com.sd.tennis.specification.PlayerSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateException(userDTO.getUsername() + " already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateException(userDTO.getEmail() + " already exists");
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!userDTO.getEmail().matches(emailRegex)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if(userDTO.getUsername().length() < 5 || userDTO.getUsername().length() > 20) {
            throw new IllegalArgumentException("Username must be between 5 and 20 characters");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getBirthDate() != null)   user.setBirthDate(userDTO.getBirthDate());
        if (userDTO.getRanking() != null)     user.setRanking(userDTO.getRanking());
        if (userDTO.getNationality() != null) user.setNationality(userDTO.getNationality());

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Integer userId, UserDTO userDTO) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        User user = userOpt.get();
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        if (userDTO.getRole() != null) {
            user.setRole(userDTO.getRole());
        }
        if (userDTO.getFirstName() != null) {
            user.setFirstName(userDTO.getFirstName());
        }
        if (userDTO.getLastName() != null) {
            user.setLastName(userDTO.getLastName());
        }
        if (userDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getBirthDate() != null)   user.setBirthDate(userDTO.getBirthDate());
        if (userDTO.getRanking() != null)     user.setRanking(userDTO.getRanking());
        if (userDTO.getNationality() != null) user.setNationality(userDTO.getNationality());

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return userOpt.get();
    }

    @Override
    public void deleteUser(Integer userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.delete(userOpt.get());
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByRole(String role) {
        Optional<List<User>> usersOpt = userRepository.findByRole(role);
        if (usersOpt.isEmpty()) {
            throw new ResourceNotFoundException("No users found with role: " + role);
        }
        return usersOpt.get();
    }

    @Override
    public void authenticateUser(LoginRequest lr) {
        Optional<User> userOpt = userRepository.findByUsername(lr.getUsername());
        if (userOpt.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(lr.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }

    @Override
    public User createUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new DuplicateException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new DuplicateException("Email already exists");
        }

        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRole(userDTO.getRole());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        if (userDTO.getBirthDate() != null)   user.setBirthDate(userDTO.getBirthDate());
        if (userDTO.getRanking() != null)     user.setRanking(userDTO.getRanking());
        if (userDTO.getNationality() != null) user.setNationality(userDTO.getNationality());

        return userRepository.save(user);
    }

    @Override
    public List<User> filterPlayers(Integer minRanking,
                                    Integer maxRanking,
                                    String nationality,
                                    Integer minAge,
                                    Integer maxAge) {

        Specification<User> spec = Specification
                .where(PlayerSpecification.hasRole("PLAYER"))
                .and(PlayerSpecification.minRanking(minRanking))
                .and(PlayerSpecification.maxRanking(maxRanking))
                .and(PlayerSpecification.nationalityEquals(nationality))
                .and(PlayerSpecification.minAge(minAge))
                .and(PlayerSpecification.maxAge(maxAge));

        return userRepository.findAll(spec);
    }
}
