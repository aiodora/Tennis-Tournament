package com.sd.tennis.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ScoreDetailDTO {
    @NotNull(message = "Match ID cannot be null")
    private Integer matchId;

    @NotNull(message = "Set number cannot be null")
    private Integer setNumber;

    @NotNull(message = "Player 1 score cannot be null")
    private Integer player1Score;

    @NotNull(message = "Player 2 score cannot be null")
    private Integer player2Score;
}
