package com.sd.tennis.service;

import com.sd.tennis.dto.MatchDTO;
import com.sd.tennis.exception.DateException;
import com.sd.tennis.exception.ForbiddenException;
import com.sd.tennis.exception.NegativeValueException;
import com.sd.tennis.exception.ResourceNotFoundException;
import com.sd.tennis.model.Match;
import com.sd.tennis.model.ScoreDetail;
import com.sd.tennis.model.Tournament;
import com.sd.tennis.model.User;
import com.sd.tennis.repository.MatchRepository;
import com.sd.tennis.repository.TournamentRepository;
import com.sd.tennis.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchServiceImpl implements MatchService {
    private final MatchRepository matchRepository;
    private final UserRepository playerRepository;
    private final UserRepository refereeRepository;
    private final TournamentRepository tournamentRepository;

    public MatchServiceImpl(MatchRepository matchRepository,
                            UserRepository playerRepository,
                            UserRepository refereeRepository,
                            TournamentRepository tournamentRepository) {
        this.matchRepository = matchRepository;
        this.playerRepository = playerRepository;
        this.refereeRepository = refereeRepository;
        this.tournamentRepository = tournamentRepository;
    }

    @Override
    public Match createMatch(MatchDTO matchDTO) {
        Tournament tournament = tournamentRepository.findById(matchDTO.getTournamentId())
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        LocalDateTime matchDate = matchDTO.getMatchDate();
        LocalDate matchDateLocal = matchDate.toLocalDate();

        if (matchDateLocal.isBefore(tournament.getStartDate()) || matchDateLocal.isAfter(tournament.getEndDate())) {
            throw new DateException("Match date must be within the tournament dates");
        }

        User player1 = playerRepository.findById(matchDTO.getPlayer1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 1 not found"));
        User player2 = playerRepository.findById(matchDTO.getPlayer2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 2 not found"));
        User referee = refereeRepository.findById(matchDTO.getRefereeId())
                .orElseThrow(() -> new ResourceNotFoundException("Referee not found"));

        List<Match> sameDayMatchesPlayer1 = matchRepository.findAllByPlayerIdAndDay(matchDTO.getPlayer1Id(), matchDTO.getMatchDate());
        if (!sameDayMatchesPlayer1.isEmpty()) {
            for (Match existing : sameDayMatchesPlayer1) {
                if (timeOverlap(existing.getMatchDate(), matchDTO.getMatchDate())) {
                    throw new ForbiddenException("Player 1 is already scheduled within 2 hours of that match.");
                }
            }
        }

        List<Match> sameDayMatchesPlayer2 = matchRepository.findAllByPlayerIdAndDay(matchDTO.getPlayer2Id(), matchDTO.getMatchDate());
        if (!sameDayMatchesPlayer2.isEmpty()) {
            for (Match existing : sameDayMatchesPlayer2) {
                if (timeOverlap(existing.getMatchDate(), matchDTO.getMatchDate())) {
                    throw new ForbiddenException("Player 2 is already scheduled within 2 hours of that match.");
                }
            }
        }

        List<Match> sameDayMatchesReferee = matchRepository.findAllByPlayerIdAndDay(matchDTO.getRefereeId(), matchDTO.getMatchDate());
        if (!sameDayMatchesReferee.isEmpty()) {
            for (Match existing : sameDayMatchesReferee) {
                if (timeOverlap(existing.getMatchDate(), matchDTO.getMatchDate())) {
                    throw new ForbiddenException("Referee is already scheduled within 2 hours of that match.");
                }
            }
        }

        if (matchDTO.getPlayer1Id().equals(matchDTO.getPlayer2Id())) {
            throw new ForbiddenException("Players cannot be the same");
        }

        if (matchRepository.existsByPlayer1IdAndPlayer2Id(matchDTO.getPlayer1Id(), matchDTO.getPlayer2Id())) {
            throw new ForbiddenException("Players are already scheduled to play against each other in the same tournament.");
        }

        Match match = new Match();
        match.setTournament(tournament);
        match.setPlayer1(player1);
        match.setPlayer2(player2);
        match.setReferee(referee);
        match.setMatchDate(matchDate);
        match.setVenue(matchDTO.getVenue());
        match.setWinner(null); //initial fara

        String dtoOverallScore = matchDTO.getOverallScore();
        match.setOverallScore((dtoOverallScore != null && !dtoOverallScore.isBlank())
                ? dtoOverallScore
                : "N/A");

        return matchRepository.save(match);
    }

    private boolean timeOverlap(LocalDateTime existing, LocalDateTime proposed) {
        long diffInMinutes = ChronoUnit.MINUTES.between(existing, proposed);
        return Math.abs(diffInMinutes) < 120;
    }

    @Override
    public Match getMatchById(Integer matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
    }

    @Override
    public Match assignRefereeToMatch(Integer matchId, Integer refereeId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        User referee = refereeRepository.findById(refereeId)
                .orElseThrow(() -> new ResourceNotFoundException("Referee not found"));

        match.setReferee(referee);
        return matchRepository.save(match);
    }

    @Override
    public List<MatchDTO> getAllMatches() {
        List<Match> matches = matchRepository.findAll();
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public List<MatchDTO> getMatchesByTournamentId(Integer tournamentId) {
        tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        List<Match> matches = matchRepository.findByTournamentId(tournamentId).orElse(List.of());
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public List<MatchDTO> getMatchesByPlayerId(Integer playerId) {
        playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found"));

        List<Match> matches = matchRepository.findByPlayer1Id(playerId).orElse(List.of());
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public List<MatchDTO> getMatchesByPlayersId(Integer p1, Integer p2) {
        playerRepository.findById(p1)
                .orElseThrow(() -> new ResourceNotFoundException("Player 1 not found"));
        playerRepository.findById(p2)
                .orElseThrow(() -> new ResourceNotFoundException("Player 2 not found"));

        List<Match> matches = matchRepository.findByPlayersId(p1, p2).orElse(List.of());
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public List<MatchDTO> getMatchesByRefereeId(Integer refereeId) {
        refereeRepository.findById(refereeId)
                .orElseThrow(() -> new ResourceNotFoundException("Referee not found"));

        List<Match> matches = matchRepository.findByRefereeId(refereeId).orElse(List.of());
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public List<MatchDTO> getMatchesByDate(String date) {
        LocalDateTime matchDate = LocalDateTime.parse(date);
        List<Match> matches = matchRepository.findByMatchDate(matchDate).orElse(List.of());
        return matches.stream().map(this::toMatchDTO).toList();
    }

    @Override
    public void updateMatch(Integer matchId, MatchDTO matchDTO) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.setMatchDate(matchDTO.getMatchDate());
        match.setVenue(matchDTO.getVenue());
        match.setWinner(matchDTO.getWinnerId() != null
                ? playerRepository.findById(matchDTO.getWinnerId()).orElse(null)
                : null);

        if (matchDTO.getOverallScore() != null && !matchDTO.getOverallScore().isBlank()) {
            match.setOverallScore(matchDTO.getOverallScore());
        }

        matchRepository.save(match);
    }

    @Override
    public void deleteMatch(Integer matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        matchRepository.delete(match);
    }

    //for future, to automatically compute the final score from ScoreDetail
    private String computeMatchOutcome(Match match) {
        int player1Sets = 0;
        int player2Sets = 0;
        StringBuilder setScores = new StringBuilder();

        for (ScoreDetail detail : match.getScoreDetails()) {
            if (detail.getPlayer1Score() > detail.getPlayer2Score()) {
                player1Sets++;
            } else if (detail.getPlayer1Score() < detail.getPlayer2Score()) {
                player2Sets++;
            }
            setScores.append("Set ")
                    .append(detail.getSetNumber())
                    .append(": ")
                    .append(detail.getPlayer1Score())
                    .append("-")
                    .append(detail.getPlayer2Score())
                    .append("; ");
        }

        String outcome;
        if (player1Sets > player2Sets) {
            outcome = "Player 1 wins: " + player1Sets + "-" + player2Sets;
            match.setWinner(match.getPlayer1());
        } else if (player1Sets < player2Sets) {
            outcome = "Player 2 wins: " + player2Sets + "-" + player1Sets;
            match.setWinner(match.getPlayer2());
        } else {
            outcome = "Tie";
        }

        match.setOverallScore(outcome + " | Set Scores: " + setScores);
        matchRepository.save(match);

        return match.getOverallScore();
    }

    @Override
    public List<MatchDTO> getFilteredMatches(Integer tournamentId, Integer playerId, Integer refereeId) {
        List<Match> matches = matchRepository.findAll();

        if (tournamentId != null) {
            matches = matches.stream()
                    .filter(m -> m.getTournament().getId().equals(tournamentId))
                    .collect(Collectors.toList());
        }
        if (playerId != null) {
            matches = matches.stream()
                    .filter(m -> m.getPlayer1().getId().equals(playerId)
                            || m.getPlayer2().getId().equals(playerId))
                    .collect(Collectors.toList());
        }
        if (refereeId != null) {
            matches = matches.stream()
                    .filter(m -> m.getReferee().getId().equals(refereeId))
                    .collect(Collectors.toList());
        }

        return matches.stream().map(this::toMatchDTO).collect(Collectors.toList());
    }

    private MatchDTO toMatchDTO(Match match) {
        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setMatchId(match.getId());
        matchDTO.setTournamentId(match.getTournament().getId());
        matchDTO.setPlayer1Id(match.getPlayer1().getId());
        matchDTO.setPlayer2Id(match.getPlayer2().getId());
        matchDTO.setRefereeId(match.getReferee().getId());
        matchDTO.setWinnerId(match.getWinner() != null ? match.getWinner().getId() : null);
        matchDTO.setMatchDate(match.getMatchDate());
        matchDTO.setVenue(match.getVenue());

        matchDTO.setTournamentName(match.getTournament().getName());
        matchDTO.setPlayer1Name(match.getPlayer1().getFirstName() + " " + match.getPlayer1().getLastName());
        matchDTO.setPlayer2Name(match.getPlayer2().getFirstName() + " " + match.getPlayer2().getLastName());
        matchDTO.setRefereeName(match.getReferee().getFirstName() + " " + match.getReferee().getLastName());
        matchDTO.setWinnerName(match.getWinner() != null
                ? (match.getWinner().getFirstName() + " " + match.getWinner().getLastName())
                : "N/A");

        matchDTO.setOverallScore(match.getOverallScore());
        return matchDTO;
    }

    public void updateOverallScore(Integer matchId, String newScore, Integer refereeId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (!match.getReferee().getId().equals(refereeId)) {
            throw new ForbiddenException("You are not the referee for this match.");
        }

        if (LocalDateTime.now().isBefore(match.getMatchDate())) {
            throw new DateException("Cannot update score before match start time.");
        }

        if (LocalDateTime.now().isAfter(match.getMatchDate().plusHours(2))) {
            throw new DateException("Cannot update score after 2 hours of match start time.");
        }

        if (!newScore.matches("\\d+-\\d+")) {
            throw new ForbiddenException("Overall score must be in the format e.g. '6-4'");
        }

        String[] scores = newScore.split("-");
        int player1Score = Integer.parseInt(scores[0]);
        int player2Score = Integer.parseInt(scores[1]);
        if (player1Score < 0 || player2Score < 0) {
            throw new NegativeValueException("Scores cannot be negative");
        }

        if (player1Score > player2Score) {
            match.setWinner(match.getPlayer1());
        } else if (player2Score > player1Score) {
            match.setWinner(match.getPlayer2());
        } else {
            match.setWinner(null);
        }

        match.setOverallScore(newScore);
        matchRepository.save(match);
    }
}
