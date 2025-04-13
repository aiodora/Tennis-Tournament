package com.sd.tennis.mapper;

import com.sd.tennis.dto.MatchResponseDTO;
import com.sd.tennis.model.Match;
import com.sd.tennis.model.User;

public class MatchMapper {
    public static MatchResponseDTO toMatchResponseDTO(Match match, String overallScore) {
        if (match == null) return null;

        MatchResponseDTO dto = new MatchResponseDTO();
        dto.setMatchId(match.getId());
        dto.setTournamentId(match.getTournament().getId());
        dto.setTournamentName(match.getTournament().getName());

        User p1 = match.getPlayer1();
        dto.setPlayer1Id(p1.getId());
        dto.setPlayer1Name(p1.getFirstName() + " " + p1.getLastName());

        User p2 = match.getPlayer2();
        dto.setPlayer2Id(p2.getId());
        dto.setPlayer2Name(p2.getFirstName() + " " + p2.getLastName());

        User ref = match.getReferee();
        dto.setRefereeId(ref.getId());
        dto.setRefereeName(ref.getFirstName() + " " + ref.getLastName());

        if (match.getWinner() != null) {
            dto.setWinnerId(match.getWinner().getId());
            dto.setWinnerName(match.getWinner().getFirstName() + " " + match.getWinner().getLastName());
        } else {
            dto.setWinnerId(null);
            dto.setWinnerName("N/A");
        }

        dto.setMatchDate(match.getMatchDate());
        dto.setVenue(match.getVenue());
        dto.setOverallScore(overallScore);

        return dto;
    }
}
