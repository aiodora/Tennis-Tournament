package com.sd.tennis.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class MatchDTO {
    @NotNull(message = "Tournament ID cannot be null")
    private Integer tournamentId;

    private Integer matchId;

    @NotNull(message = "Player 1 ID cannot be null")
    private Integer player1Id;

    @NotNull(message = "Player 2 ID cannot be null")
    private Integer player2Id;

    @NotNull(message = "Referee ID cannot be null")
    private Integer refereeId;

    private Integer winnerId;

    @NotNull(message = "Match date cannot be null")
    private LocalDateTime matchDate;

    @NotNull(message = "Venue cannot be null")
    private String venue;

    @Size(max = 4, message = "Overall score cannot exceed 4 characters")
    @Pattern(regexp = "\\d-\\d", message = "Overall score must be in the format 'X-Y'")
    private String overallScore;

    private String tournamentName;
    private String player1Name;
    private String player2Name;
    private String refereeName;
    private String winnerName;
}
