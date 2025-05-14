package com.sd.tennis.service;

import com.sd.tennis.dto.MatchDTO;
import com.sd.tennis.exception.DateException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Match;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.MatchRepository;
import com.sd.tennis.repository.TournamentRepository;
import com.sd.tennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository playerRepository;      // must match service field name

    @Mock
    private UserRepository refereeRepository;     // must match service field name

    @Mock
    private TournamentRepository tournamentRepository; // must match service field name

    @InjectMocks
    private MatchServiceImpl service;

    private Tournament tournament;
    private User p1, p2, ref;

    @BeforeEach
    void setUp() {
        // Tournament running May 1–31, 2025
        tournament = new Tournament();
        tournament.setId(1);
        tournament.setStartDate(LocalDate.of(2025, 5, 1));
        tournament.setEndDate(LocalDate.of(2025, 5, 31));

        // Two players
        p1 = new User();
        p1.setId(10);
        p2 = new User();
        p2.setId(11);

        // Referee with ID 20
        ref = new User();
        ref.setId(20);
    }

    @Test
    void createMatch_success() {
        // Stub out tournament + users + no conflicting matches
        when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
        when(playerRepository.findById(10)).thenReturn(Optional.of(p1));
        when(playerRepository.findById(11)).thenReturn(Optional.of(p2));
        when(refereeRepository.findById(20)).thenReturn(Optional.of(ref));
        when(matchRepository.findAllByPlayerIdAndDay(anyInt(), any()))
                .thenReturn(emptyList());

        // Prepare DTO
        MatchDTO dto = new MatchDTO();
        dto.setTournamentId(1);
        dto.setPlayer1Id(10);
        dto.setPlayer2Id(11);
        dto.setRefereeId(20);  // must match ref.getId()
        dto.setMatchDate(LocalDateTime.of(2025, 5, 15, 10, 0));
        dto.setVenue("Court 1");

        // Saving should just return the entity
        when(matchRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Match m = service.createMatch(dto);

        assertThat(m.getTournament()).isSameAs(tournament);
        assertThat(m.getPlayer1()).isSameAs(p1);
        assertThat(m.getPlayer2()).isSameAs(p2);
        assertThat(m.getReferee()).isSameAs(ref);
    }

    @Test
    void createMatch_dateOutsideTournament_throws() {
        when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));

        MatchDTO dto = new MatchDTO();
        dto.setTournamentId(1);
        dto.setPlayer1Id(10);
        dto.setPlayer2Id(11);
        dto.setRefereeId(20);
        // June 1st is outside the May 1–31 window
        dto.setMatchDate(LocalDateTime.of(2025, 6, 1, 10, 0));

        assertThatThrownBy(() -> service.createMatch(dto))
                .isInstanceOf(DateException.class)
                .hasMessageContaining("within the tournament dates");
    }

    @Test
    void getMatchById_notFound() {
        when(matchRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMatchById(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Match not found");
    }

    @Test
    void assignRefereeToMatch_success() {
        // Existing match with no referee
        Match existing = new Match();
        existing.setId(5);
        when(matchRepository.findById(5)).thenReturn(Optional.of(existing));

        // Stub the referee lookup with ID 20
        when(refereeRepository.findById(20)).thenReturn(Optional.of(ref));
        when(matchRepository.save(existing)).thenReturn(existing);

        Match updated = service.assignRefereeToMatch(5, 20);

        assertThat(updated.getReferee()).isSameAs(ref);
    }
}
