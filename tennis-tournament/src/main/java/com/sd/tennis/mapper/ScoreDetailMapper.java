package com.sd.tennis.mapper;

import com.sd.tennis.dto.ScoreDetailResponseDTO;
import com.sd.tennis.model.ScoreDetail;

public class ScoreDetailMapper {

    public static ScoreDetailResponseDTO toScoreDetailResponseDTO(ScoreDetail sd) {
        if (sd == null) return null;

        ScoreDetailResponseDTO dto = new ScoreDetailResponseDTO();
        dto.setId(sd.getId());
        dto.setMatchId(sd.getMatch().getId());
        dto.setSetNumber(sd.getSetNumber());
        dto.setPlayer1Score(sd.getPlayer1Score());
        dto.setPlayer2Score(sd.getPlayer2Score());
        return dto;
    }
}
