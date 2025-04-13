package com.sd.tennis.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentResponseDTO {
    private Integer id;
    private String name;
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String status;
    private LocalDate registrationDeadline;

    private List<MatchResponseDTO> matches;
    private List<RegistrationResponseDTO> registrations;
}
