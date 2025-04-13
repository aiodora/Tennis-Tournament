package com.sd.tennis.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchResponseDTO {
    private Integer matchId;
    private Integer tournamentId;
    private String tournamentName;

    private Integer player1Id;
    private String player1Name;
    private Integer player2Id;
    private String player2Name;

    private Integer refereeId;
    private String refereeName;

    private Integer winnerId;
    private String winnerName;

    private LocalDateTime matchDate;
    private String venue;
    private String overallScore;
}
