package com.sd.tennis.mapper;

import com.sd.tennis.dto.TournamentResponseDTO;
import com.sd.tennis.model.Tournament;

import java.util.Collections;
import java.util.stream.Collectors;

public class TournamentMapper {

    public static TournamentResponseDTO toTournamentResponseDTO(Tournament t) {
        if (t == null) return null;
        TournamentResponseDTO dto = new TournamentResponseDTO();
        dto.setId(t.getId());
        dto.setName(t.getName());
        dto.setLocation(t.getLocation());
        dto.setStartDate(t.getStartDate());
        dto.setEndDate(t.getEndDate());
        dto.setDescription(t.getDescription());
        dto.setStatus(t.getStatus());
        dto.setRegistrationDeadline(t.getRegistrationDeadline());

        return dto;
    }
}
