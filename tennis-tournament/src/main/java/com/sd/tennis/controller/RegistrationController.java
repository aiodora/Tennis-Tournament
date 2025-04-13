package com.sd.tennis.controller;

import com.sd.tennis.dto.RegistrationResponseDTO;
import com.sd.tennis.mapper.RegistrationMapper;
import com.sd.tennis.model.Registration;
import com.sd.tennis.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/player/{playerId}/tournament/{tournamentId}")
    @PreAuthorize("hasAuthority('PLAYER')")
    public ResponseEntity<RegistrationResponseDTO> registerForTournament(@PathVariable Integer playerId,
                                                                         @PathVariable Integer tournamentId) {
        var reg = registrationService.registerPlayer(playerId, tournamentId);
        return ResponseEntity.ok(RegistrationMapper.toRegistrationResponseDTO(reg));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RegistrationResponseDTO>> getAllRegistrations() {
        var list = registrationService.getAllRegistrations().stream()
                .map(RegistrationMapper::toRegistrationResponseDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/player/{playerId}")
    @PreAuthorize("hasAuthority('PLAYER') or hasAuthority('ADMIN')")
    public ResponseEntity<List<RegistrationResponseDTO>> getRegistrationsByPlayer(@PathVariable Integer playerId) {
        var regs = registrationService
                .getRegistrationsByPlayerId(playerId)
                .orElse(List.of());
        var dtoList = regs.stream()
                .map(RegistrationMapper::toRegistrationResponseDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/tournament/{tournamentId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<RegistrationResponseDTO>> getRegistrationsByTournament(@PathVariable Integer tournamentId) {
        var regs = registrationService
                .getRegistrationsByTournamentId(tournamentId)
                .orElse(List.of());
        var dtoList = regs.stream()
                .map(RegistrationMapper::toRegistrationResponseDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
}