package com.sd.tennis.service;

import com.sd.tennis.dto.TournamentDTO;
import com.sd.tennis.exception.DuplicateException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.repository.TournamentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentServiceImplTest {
    @Mock TournamentRepository repo;
    @InjectMocks TournamentServiceImpl service;

    TournamentDTO dto;
    Tournament t;

    @BeforeEach
    void setUp() {
        dto = new TournamentDTO();
        dto.setName("Open");
        dto.setLocation("Court");
        dto.setStartDate(LocalDate.of(2025,5,1));
        dto.setEndDate(LocalDate.of(2025,5,30));
        dto.setStatus("UPCOMING");
        dto.setDescription("Desc");
        dto.setRegistrationDeadline(LocalDate.of(2025,4,30));

        t = new Tournament(); t.setId(1);
    }

    @Test
    void createTournament_success() {
        when(repo.existsByName("Open")).thenReturn(false);
        when(repo.save(any())).thenReturn(t);

        Tournament res = service.createTournament("Open", dto);
        assertThat(res).isEqualTo(t);
    }

    @Test
    void createTournament_duplicate_throws() {
        when(repo.existsByName("Open")).thenReturn(true);
        assertThatThrownBy(() -> service.createTournament("Open", dto))
                .isInstanceOf(DuplicateException.class);
    }

    @Test
    void getTournamentDetails_notFound() {
        when(repo.findById(1)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getTournamentDetails(1))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}