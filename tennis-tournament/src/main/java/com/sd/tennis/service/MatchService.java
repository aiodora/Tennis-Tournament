package com.sd.tennis.service;

import com.sd.tennis.dto.MatchDTO;
import com.sd.tennis.model.Match;

import java.util.List;

public interface MatchService {
    Match createMatch(MatchDTO matchDTO);
    Match getMatchById(Integer matchId);
    Match assignRefereeToMatch(Integer matchId, Integer refereeId);
    List<MatchDTO> getAllMatches();
    List<MatchDTO> getMatchesByTournamentId(Integer tournamentId);
    List<MatchDTO> getMatchesByPlayerId(Integer playerId);
    List<MatchDTO> getMatchesByPlayersId(Integer player1Id, Integer player2Id);
    List<MatchDTO> getMatchesByRefereeId(Integer refereeId);
    List<MatchDTO> getMatchesByDate(String date);
    void updateMatch(Integer matchId, MatchDTO matchDTO);
    void deleteMatch(Integer matchId);
    List<MatchDTO> getFilteredMatches(Integer tournamentId, Integer playerId, Integer refereeId);
    void updateOverallScore(Integer matchId, String overallScore, Integer refereeId);
}
