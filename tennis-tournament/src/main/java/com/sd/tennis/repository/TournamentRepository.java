package com.sd.tennis.repository;

import com.sd.tennis.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    Optional<Tournament> findByName(String name);
    Optional<List<Tournament>> findByLocation(String location);
    Optional<List<Tournament>> findByStartDate(LocalDate startDate);
    Optional<List<Tournament>> findByEndDate(LocalDate endDate);
    Optional<List<Tournament>> findByStatus(String status);
    boolean existsByName(String name);
}
