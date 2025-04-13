package com.sd.tennis.mapper;

import com.sd.tennis.dto.RegistrationResponseDTO;
import com.sd.tennis.model.Registration;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;

public class RegistrationMapper {

    public static RegistrationResponseDTO toRegistrationResponseDTO(Registration reg) {
        if (reg == null) return null;

        RegistrationResponseDTO dto = new RegistrationResponseDTO();
        dto.setId(reg.getId());

        User player = reg.getPlayer();
        dto.setPlayerId(player.getId());
        dto.setPlayerUsername(player.getUsername());

        Tournament t = reg.getTournament();
        dto.setTournamentId(t.getId());
        dto.setTournamentName(t.getName());

        dto.setRegistrationDate(reg.getRegistrationDate());
        dto.setStatus(reg.getStatus());

        return dto;
    }
}
