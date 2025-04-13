package com.sd.tennis.controller;

import com.sd.tennis.dto.MatchDTO;
import com.sd.tennis.dto.MatchResponseDTO;
import com.sd.tennis.mapper.MatchMapper;
import com.sd.tennis.model.Match;
import com.sd.tennis.model.User;
import com.sd.tennis.service.MatchService;
import com.sd.tennis.util.CsvExportUtil;
import com.sd.tennis.util.ExportStrategy;
import com.sd.tennis.util.TxtExportUtil;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MatchResponseDTO> createMatch(@Valid @RequestBody MatchDTO matchDTO) {
        Match created = matchService.createMatch(matchDTO);
        MatchResponseDTO responseDTO = MatchMapper.toMatchResponseDTO(created, created.getOverallScore());
        return ResponseEntity.ok(responseDTO);
    }

    @PermitAll
    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> getAllMatches() {
        List<MatchDTO> matchDTOs = matchService.getAllMatches();
        List<MatchResponseDTO> responseList = matchDTOs.stream().map(mDto -> {
            Match entity = matchService.getMatchById(mDto.getMatchId());
            return MatchMapper.toMatchResponseDTO(entity, entity.getOverallScore());
        }).toList();
        return ResponseEntity.ok(responseList);
    }

    @PermitAll
    @GetMapping("/{matchId}")
    public ResponseEntity<MatchResponseDTO> getMatch(@PathVariable Integer matchId) {
        Match match = matchService.getMatchById(matchId);
        MatchResponseDTO dto = MatchMapper.toMatchResponseDTO(match, match.getOverallScore());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{matchId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> updateMatch(@PathVariable Integer matchId,
                                              @Valid @RequestBody MatchDTO matchDTO) {
        matchService.updateMatch(matchId, matchDTO);
        return ResponseEntity.ok("Match updated successfully");
    }

    @DeleteMapping("/{matchId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> deleteMatch(@PathVariable Integer matchId) {
        matchService.deleteMatch(matchId);
        return ResponseEntity.ok("Match deleted successfully");
    }

    @PutMapping("/{matchId}/assign-referee/{refereeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<MatchResponseDTO> assignReferee(@PathVariable Integer matchId,
                                                          @PathVariable Integer refereeId) {
        Match updated = matchService.assignRefereeToMatch(matchId, refereeId);
        MatchResponseDTO dto = MatchMapper.toMatchResponseDTO(updated, updated.getOverallScore());
        return ResponseEntity.ok(dto);
    }

    @PermitAll
    @GetMapping("/tournament/{tournamentId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByTournament(@PathVariable Integer tournamentId) {
        List<MatchDTO> matchDTOs = matchService.getMatchesByTournamentId(tournamentId);
        List<MatchResponseDTO> responseList = matchDTOs.stream().map(mDto -> {
            Match entity = matchService.getMatchById(mDto.getMatchId());
            return MatchMapper.toMatchResponseDTO(entity, entity.getOverallScore());
        }).toList();
        return ResponseEntity.ok(responseList);
    }

    @PermitAll
    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByPlayer(@PathVariable Integer playerId) {
        List<MatchDTO> matchDTOs = matchService.getMatchesByPlayerId(playerId);
        List<MatchResponseDTO> responseList = matchDTOs.stream().map(mDto -> {
            Match entity = matchService.getMatchById(mDto.getMatchId());
            return MatchMapper.toMatchResponseDTO(entity, entity.getOverallScore());
        }).toList();
        return ResponseEntity.ok(responseList);
    }

    @PermitAll
    @GetMapping("/referee/{refereeId}")
    public ResponseEntity<List<MatchResponseDTO>> getMatchesByReferee(@PathVariable Integer refereeId) {
        List<MatchDTO> matchDTOs = matchService.getMatchesByRefereeId(refereeId);
        List<MatchResponseDTO> responseList = matchDTOs.stream().map(mDto -> {
            Match entity = matchService.getMatchById(mDto.getMatchId());
            return MatchMapper.toMatchResponseDTO(entity, entity.getOverallScore());
        }).toList();
        return ResponseEntity.ok(responseList);
    }

    @PermitAll
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> exportMatches(
            @RequestParam String format,
            @RequestParam(required = false) Integer tournamentId,
            @RequestParam(required = false) Integer playerId,
            @RequestParam(required = false) Integer refereeId) {
        List<MatchDTO> matches = matchService.getFilteredMatches(tournamentId, playerId, refereeId);
        ExportStrategy strategy;
        if ("csv".equalsIgnoreCase(format)) {
            strategy = new CsvExportUtil();
        } else if ("txt".equalsIgnoreCase(format)) {
            strategy = new TxtExportUtil();
        } else {
            return ResponseEntity.badRequest().body("Invalid format. Use 'csv' or 'txt'.");
        }
        String output = strategy.export(matches);
        return ResponseEntity.ok(output);
    }

    @PutMapping("/{matchId}/update-score")
    @PreAuthorize("hasAuthority('REFEREE')")
    public ResponseEntity<MatchResponseDTO> updateScore(@PathVariable Integer matchId,
                                                        @RequestParam String overallScore,
                                                        Authentication authentication) {
        Integer refereeId = ((User) authentication.getPrincipal()).getId();
        matchService.updateOverallScore(matchId, overallScore, refereeId);
        Match updated = matchService.getMatchById(matchId);
        MatchResponseDTO dto = MatchMapper.toMatchResponseDTO(updated, updated.getOverallScore());
        return ResponseEntity.ok(dto);
    }
}
