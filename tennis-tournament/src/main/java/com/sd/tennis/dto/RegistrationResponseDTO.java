package com.sd.tennis.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RegistrationResponseDTO {
    private Integer id;
    private Integer playerId;
    private String playerUsername;
    private Integer tournamentId;
    private String tournamentName;
    private LocalDateTime registrationDate;
    private String status;
    private LocalDateTime decisionDate;
    private Boolean notificationSent;
}
