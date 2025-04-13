package com.sd.tennis.repository;

import com.sd.tennis.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match, Integer> {
    Optional<List<Match>> findByPlayer1Id(Integer player1Id);
    @Query("SELECT m FROM Match m WHERE (m.player1.id = :p1 AND m.player2.id = :p2) OR (m.player1.id = :p2 AND m.player2.id = :p1)")
    Optional<List<Match>> findByPlayersId(@Param("p1") Integer p1, @Param("p2") Integer p2);
    Optional<List<Match>> findByTournamentId(Integer tournamentId);
    Optional<List<Match>> findByRefereeId(Integer refereeId);
    Optional<List<Match>> findByMatchDate(LocalDateTime matchDate);
    boolean existsByPlayer1IdAndPlayer2Id(Integer player1Id, Integer player2Id);
    @Query("SELECT m FROM Match m WHERE (m.player1.id = :playerId OR m.player2.id = :playerId) AND DATE(m.matchDate) = DATE(:matchDate)")
    List<Match> findAllByPlayerIdAndDay(@Param("playerId") Integer playerId, @Param("matchDate") LocalDateTime matchDate);
}
