package com.sd.tennis.util;

import com.sd.tennis.dto.MatchDTO;

import java.util.List;

public class TxtExportUtil implements ExportStrategy {
    @Override
    public String export(List<MatchDTO> matches) {
        StringBuilder txtData = new StringBuilder();
        for (MatchDTO match : matches) {
            txtData.append("Match ID: ").append(match.getMatchId()).append("\n")
                    .append("Tournament Name: ").append(match.getTournamentName()).append("\n")
                    .append("Player 1: ").append(match.getPlayer1Name()).append("\n")
                    .append("Player 2: ").append(match.getPlayer2Name()).append("\n")
                    .append("Winner: ").append(match.getWinnerName()).append("\n")
                    .append("Match Date: ").append(match.getMatchDate()).append("\n")
                    .append("Venue: ").append(match.getVenue()).append("\n")
                    .append("Overall Score: ").append(match.getOverallScore()).append("\n\n")
                    .append("--------------------------------------------------\n");
        }
        return txtData.toString();
    }
}
