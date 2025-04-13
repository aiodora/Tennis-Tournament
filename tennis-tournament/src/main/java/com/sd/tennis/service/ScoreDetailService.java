package com.sd.tennis.service;

import com.sd.tennis.dto.ScoreDetailDTO;
import com.sd.tennis.model.ScoreDetail;

import java.util.List;

public interface ScoreDetailService {
    ScoreDetail addScoreDetail(ScoreDetailDTO scoreDetailDTO);
    ScoreDetail getScoreDetailById(Integer id);
    List<ScoreDetail> getAllScoreDetails();
    ScoreDetail updateScoreDetail(Integer id, ScoreDetailDTO scoreDetailDTO);
    void deleteScoreDetail(Integer id);
}
