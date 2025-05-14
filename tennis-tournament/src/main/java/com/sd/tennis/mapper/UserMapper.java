package com.sd.tennis.mapper;

import com.sd.tennis.dto.UserResponseDTO;
import com.sd.tennis.model.User;

public class UserMapper {

    public static UserResponseDTO toUserResponseDTO(User user) {
        if (user == null) return null;
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getContactInfo());
        dto.setBirthDate(user.getBirthDate());
        dto.setRanking(user.getRanking());
        dto.setNationality(user.getNationality());
        return dto;
    }
}
