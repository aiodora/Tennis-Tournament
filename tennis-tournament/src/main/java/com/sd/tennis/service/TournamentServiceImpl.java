package com.sd.tennis.service;

import com.sd.tennis.dto.TournamentDTO;
import com.sd.tennis.exception.DuplicateException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.repository.TournamentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class TournamentServiceImpl implements TournamentService{
    private final TournamentRepository tournamentRepository;

    @Autowired
    public TournamentServiceImpl(TournamentRepository tournamentRepository) {
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Tournament createTournament(String tournamentName, TournamentDTO tournamentDTO) {
        if (tournamentRepository.existsByName(tournamentName)) {
            throw new DuplicateException("Tournament with name " + tournamentName + " already exists");
        }

        Tournament tournament = new Tournament();
        tournament.setName(tournamentDTO.getName());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());
        tournament.setStatus(tournamentDTO.getStatus());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setRegistrationDeadline(tournamentDTO.getRegistrationDeadline());

        return tournamentRepository.save(tournament);
    }

    @Override
    public void updateTournament(Integer tournamentId, TournamentDTO tournamentDTO) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));

        tournament.setName(tournamentDTO.getName());
        tournament.setLocation(tournamentDTO.getLocation());
        tournament.setStartDate(tournamentDTO.getStartDate());
        tournament.setEndDate(tournamentDTO.getEndDate());
        tournament.setStatus(tournamentDTO.getStatus());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setRegistrationDeadline(tournamentDTO.getRegistrationDeadline());

        tournamentRepository.save(tournament);
    }

    @Override
    public void deleteTournament(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));
        tournamentRepository.delete(tournament);
    }

    @Override
    public Tournament getTournamentDetails(Integer tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with id: " + tournamentId));
    }

    @Override
    public List<Tournament> listAllTournaments() {
        return tournamentRepository.findAll();
    }

    @Override
    public void saveTournament(Tournament t) {
        tournamentRepository.save(t);
    }
}
