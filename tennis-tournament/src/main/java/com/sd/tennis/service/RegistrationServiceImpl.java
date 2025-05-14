package com.sd.tennis.service;

import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Registration;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.RegistrationRepository;
import com.sd.tennis.repository.TournamentRepository;
import com.sd.tennis.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService{
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;
    private final TournamentRepository tournamentRepository;
    private final EmailService emailService;

    public RegistrationServiceImpl(RegistrationRepository registrationRepository, UserRepository userRepository, TournamentRepository tournamentRepository, EmailService emailService) {
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
        this.tournamentRepository = tournamentRepository;
        this.emailService = emailService;
    }

    @Override
    public Registration approveRegistration(Integer id) {
        System.out.println("Approving registration with ID: " + id);
        var reg = getRegistrationById(id);
        reg.setStatus("APPROVED");
        reg.setDecisionDate(Instant.now());
        reg.setNotificationSent(true);
        reg = registrationRepository.save(reg);
        emailService.sendRegistrationStatusEmail(
                reg.getPlayer().getEmail(), reg.getPlayer().getFirstName() + " " + reg.getPlayer().getLastName(),
                reg.getTournament().getName(), reg.getId(),
                true);
        return reg;
    }

    @Override
    public Registration denyRegistration(Integer id) {
        var reg = getRegistrationById(id);
        reg.setStatus("DENIED");
        reg.setDecisionDate(Instant.now());
        reg.setNotificationSent(true);
        reg = registrationRepository.save(reg);
        emailService.sendRegistrationStatusEmail(
                reg.getPlayer().getEmail(), reg.getPlayer().getFirstName() + " " + reg.getPlayer().getLastName(),
                reg.getTournament().getName(), reg.getId(),
                false);
        return reg;
    }

    @Override
    public Registration registerPlayer(Integer playerId, Integer tournamentId) {
        Registration registration = new Registration();
        registration.setPlayer(userRepository.findById(playerId).orElseThrow(() -> new ResourceNotFoundException("Player not found")));
        registration.setTournament(tournamentRepository.findById(tournamentId).orElseThrow(() -> new ResourceNotFoundException("Tournament not found")));
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus("PENDING");

        return registrationRepository.save(registration);
    }

    @Override
    public Registration getRegistrationById(Integer id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID " + id));
    }

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public Optional<List<Registration>> getRegistrationsByTournamentId(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found with ID " + tournamentId));

        return registrationRepository.findByTournamentId(tournamentId);
    }

    @Override
    public Optional<List<Registration>> getRegistrationsByPlayerId(Integer playerId) {
        User player = userRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found with ID " + playerId));

        return Optional.of(registrationRepository.findByPlayerId(playerId)
                .orElse(List.of()));
    }

    @Override
    public void updateRegistration(Integer id, Registration registration) {
        Registration existingRegistration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID " + id));

        existingRegistration.setPlayer(registration.getPlayer());
        existingRegistration.setTournament(registration.getTournament());

        registrationRepository.save(existingRegistration);
    }

    @Override
    public void deleteRegistration(Integer id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found with ID " + id));

        registrationRepository.delete(registration);
    }
}
