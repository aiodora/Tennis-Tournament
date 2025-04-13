package com.sd.tennis.dto;

import lombok.Data;

@Data
public class ScoreDetailResponseDTO {
    private Integer id;
    private Integer matchId;
    private Integer setNumber;
    private Integer player1Score;
    private Integer player2Score;
}
