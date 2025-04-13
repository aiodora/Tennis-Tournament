package com.sd.tennis.service;

import com.sd.tennis.dto.TournamentDTO;
import com.sd.tennis.model.Tournament;
import java.util.List;

public interface TournamentService {
    Tournament createTournament(String tournamentName, TournamentDTO tournamentDto);
    void updateTournament(Integer tournamentId, TournamentDTO tournamentDto);
    void deleteTournament(Integer tournamentId);
    Tournament getTournamentDetails(Integer tournamentId);
    List<Tournament> listAllTournaments();

    void saveTournament(Tournament t);
}
