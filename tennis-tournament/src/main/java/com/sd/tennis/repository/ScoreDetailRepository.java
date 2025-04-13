package com.sd.tennis.repository;

import com.sd.tennis.model.ScoreDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoreDetailRepository extends JpaRepository<ScoreDetail, Integer> {
    Optional<ScoreDetail> findByMatchId(Integer matchId);
    //Optional<ScoreDetail> findByPlayerId(Integer playerId);
    Optional<ScoreDetail> findBySetNumber(Integer setNumber);
    boolean existsByMatchId(Integer matchId);
}
