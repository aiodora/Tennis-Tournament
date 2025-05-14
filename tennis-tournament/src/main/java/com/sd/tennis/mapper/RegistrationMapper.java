package com.sd.tennis.mapper;

import com.sd.tennis.dto.RegistrationResponseDTO;
import com.sd.tennis.model.Registration;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;

import java.time.ZoneId;

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
        dto.setDecisionDate(
                reg.getDecisionDate()==null
                        ? null
                        : reg.getDecisionDate().atZone(ZoneId.systemDefault()).toLocalDateTime()
        );
        dto.setNotificationSent(reg.getNotificationSent());

        return dto;
    }
}
