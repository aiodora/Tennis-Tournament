package com.sd.tennis.controller;

import com.sd.tennis.dto.TournamentDTO;
import com.sd.tennis.dto.TournamentResponseDTO;
import com.sd.tennis.mapper.TournamentMapper;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.service.TournamentService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<TournamentResponseDTO> createTournament(@RequestBody TournamentDTO tournamentDTO) {
        var created = tournamentService.createTournament(tournamentDTO.getName(), tournamentDTO);
        return ResponseEntity.ok(TournamentMapper.toTournamentResponseDTO(created));
    }

    @PermitAll
    @GetMapping("/all")
    public ResponseEntity<List<TournamentResponseDTO>> getAllTournaments() {
        var tournaments = tournamentService.listAllTournaments();
        var dtoList = tournaments.stream()
                .map(TournamentMapper::toTournamentResponseDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PermitAll
    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponseDTO> getTournament(@PathVariable Integer id) {
        var t = tournamentService.getTournamentDetails(id);
        return ResponseEntity.ok(TournamentMapper.toTournamentResponseDTO(t));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateTournament(@PathVariable Integer id, @RequestBody TournamentDTO tournamentDTO) {
        tournamentService.updateTournament(id, tournamentDTO);
        return ResponseEntity.ok("Tournament updated successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteTournament(@PathVariable Integer id) {
        tournamentService.deleteTournament(id);
        return ResponseEntity.ok("Tournament deleted successfully");
    }
}
