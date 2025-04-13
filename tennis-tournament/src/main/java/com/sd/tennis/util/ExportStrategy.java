package com.sd.tennis.util;

import com.sd.tennis.dto.MatchDTO;

import java.util.List;

public interface ExportStrategy {
    String export(List<MatchDTO> matches);
}
