package com.sd.tennis.util;

import com.sd.tennis.dto.MatchDTO;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvExportUtil implements ExportStrategy{
    @Override
    public String export(List<MatchDTO> matches) {
        StringBuilder csvData = new StringBuilder();
        csvData.append("Match ID,Tournament Name,Player 1,Player 2,Winner,Match Date,Venue,Overall Score\n");
        for(MatchDTO match : matches) {
            csvData.append(match.getMatchId()).append(",")
                    .append(match.getTournamentName()).append(",")
                    .append(match.getPlayer1Name()).append(",")
                    .append(match.getPlayer2Name()).append(",")
                    .append(match.getWinnerName()).append(",")
                    .append(match.getMatchDate()).append(",")
                    .append(match.getVenue()).append(",")
                    .append(match.getOverallScore()).append("\n");
        }

        return csvData.toString();
    }
}
