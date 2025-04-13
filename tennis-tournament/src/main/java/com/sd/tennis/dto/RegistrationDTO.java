package com.sd.tennis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
public class RegistrationDTO {
    @NotNull(message = "Player ID cannot be null")
    private Integer playerId;

    @NotNull(message = "Tournament ID cannot be null")
    private Integer tournamentId;

    private LocalDateTime registrationDate;

    private String status = "PENDING";
}
