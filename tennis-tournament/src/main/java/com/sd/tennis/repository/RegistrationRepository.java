package com.sd.tennis.repository;

import com.sd.tennis.model.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Integer> {
    Optional<List<Registration>> findByPlayerId(Integer playerId);
    Optional<List<Registration>> findByTournamentId(Integer tournamentId);
    Optional<List<Registration>> findByStatus(String status);
    boolean existsByPlayerIdAndTournamentId(Integer playerId, Integer tournamentId);
}
