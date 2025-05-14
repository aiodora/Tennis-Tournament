package com.sd.tennis.service;

import com.sd.tennis.model.Registration;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.RegistrationRepository;
import com.sd.tennis.repository.TournamentRepository;
import com.sd.tennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {
    @Mock RegistrationRepository regRepo;
    @Mock UserRepository userRepo;
    @Mock TournamentRepository tourRepo;
    @Mock EmailService emailService;

    @InjectMocks
    RegistrationServiceImpl service;

    User player;
    Tournament tour;
    Registration reg;

    @BeforeEach
    void setUp() {
        player = new User();
        player.setEmail("p@example.com");
        // now also set names so that the test can verify playerName
        player.setFirstName("Alice");
        player.setLastName("Wonderland");

        tour = new Tournament();
        tour.setName("Open");

        reg = new Registration();
        reg.setId(1);
        reg.setPlayer(player);
        reg.setTournament(tour);
    }

    @Test
    void denyRegistration_sendsEmailAndUpdatesStatus() {
        when(regRepo.findById(1)).thenReturn(Optional.of(reg));
        when(regRepo.save(any())).thenReturn(reg);

        Registration result = service.denyRegistration(1);

        assertThat(result.getStatus()).isEqualTo("DENIED");
        // now expecting the new 5-arg signature:
        verify(emailService).sendRegistrationStatusEmail(
                "p@example.com",
                "Alice Wonderland",
                "Open",
                1,
                false
        );
    }

    @Test
    void registerPlayer_success() {
        when(userRepo.findById(2)).thenReturn(Optional.of(player));
        when(tourRepo.findById(3)).thenReturn(Optional.of(tour));
        when(regRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Registration newReg = service.registerPlayer(2, 3);
        assertThat(newReg.getPlayer()).isEqualTo(player);
        assertThat(newReg.getTournament()).isEqualTo(tour);
        assertThat(newReg.getStatus()).isEqualTo("PENDING");
    }
}
