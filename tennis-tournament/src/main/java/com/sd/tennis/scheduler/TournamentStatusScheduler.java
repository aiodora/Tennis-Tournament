package com.sd.tennis.scheduler;

import com.sd.tennis.dto.TournamentDTO;
import com.sd.tennis.service.TournamentService;
import com.sd.tennis.model.Tournament;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class TournamentStatusScheduler {
    private final TournamentService tournamentService;

    public TournamentStatusScheduler(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @Scheduled(fixedRate = 21600000)
    public void updateTournamentStatus() {
        List<Tournament> tournaments = tournamentService.listAllTournaments();
        for (Tournament t : tournaments) {
            if (t.getEndDate() != null && t.getEndDate().isBefore(LocalDate.now())) {
                t.setStatus("Completed");
            } else if (t.getStartDate() != null && t.getStartDate().isAfter(LocalDate.now())) {
                t.setStatus("Upcoming");
            } else {
                t.setStatus("Ongoing");
            }
            tournamentService.saveTournament(t);
        }
    }
}
