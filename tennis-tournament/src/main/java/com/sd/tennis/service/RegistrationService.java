package com.sd.tennis.service;

import com.sd.tennis.model.Registration;

import java.util.List;
import java.util.Optional;

public interface RegistrationService {
    Registration approveRegistration(Integer id);
    Registration denyRegistration(Integer id);
    Registration registerPlayer(Integer playerId, Integer tournamentId);
    Registration getRegistrationById(Integer id);
    List<Registration> getAllRegistrations();
    Optional<List<Registration>> getRegistrationsByTournamentId(Integer tournamentId);
    Optional<List<Registration>> getRegistrationsByPlayerId(Integer playerId);
    void updateRegistration(Integer id, Registration registration);
    void deleteRegistration(Integer id);
}
