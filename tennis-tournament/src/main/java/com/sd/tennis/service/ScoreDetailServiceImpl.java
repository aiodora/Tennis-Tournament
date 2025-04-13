package com.sd.tennis.service;

import com.sd.tennis.dto.ScoreDetailDTO;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Match;
import com.sd.tennis.model.ScoreDetail;
import com.sd.tennis.repository.MatchRepository;
import com.sd.tennis.repository.ScoreDetailRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ScoreDetailServiceImpl implements ScoreDetailService{
    private final ScoreDetailRepository scoreDetailRepository;
    private final MatchRepository matchRepository;

    public ScoreDetailServiceImpl(ScoreDetailRepository scoreDetailRepository, MatchRepository matchRepository) {
        this.scoreDetailRepository = scoreDetailRepository;
        this.matchRepository = matchRepository;
    }

    @Override
    public ScoreDetail addScoreDetail(ScoreDetailDTO scoreDetailDTO) {
        Match match = matchRepository.findById(scoreDetailDTO.getMatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID " + scoreDetailDTO.getMatchId()));

        ScoreDetail scoreDetail = new ScoreDetail();
        scoreDetail.setMatch(match);
        scoreDetail.setSetNumber(scoreDetailDTO.getSetNumber());
        scoreDetail.setPlayer1Score(scoreDetailDTO.getPlayer1Score());
        scoreDetail.setPlayer2Score(scoreDetailDTO.getPlayer2Score());

        return scoreDetailRepository.save(scoreDetail);
    }

    @Override
    public ScoreDetail getScoreDetailById(Integer id) {
        return scoreDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ScoreDetail not found with ID " + id));
    }

    @Override
    public List<ScoreDetail> getAllScoreDetails() {
        return scoreDetailRepository.findAll();
    }

    @Override
    public ScoreDetail updateScoreDetail(Integer id, ScoreDetailDTO scoreDetailDTO) {
        ScoreDetail existingDetail = getScoreDetailById(id);

        existingDetail.setSetNumber(scoreDetailDTO.getSetNumber());
        existingDetail.setPlayer1Score(scoreDetailDTO.getPlayer1Score());
        existingDetail.setPlayer2Score(scoreDetailDTO.getPlayer2Score());

        return scoreDetailRepository.save(existingDetail);
    }

    @Override
    public void deleteScoreDetail(Integer id) {
        ScoreDetail scoreDetail = scoreDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ScoreDetail not found with ID " + id));
        scoreDetailRepository.delete(scoreDetail);
    }
}
