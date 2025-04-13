package com.sd.tennis.controller;

import com.sd.tennis.dto.ScoreDetailDTO;
import com.sd.tennis.dto.ScoreDetailResponseDTO;
import com.sd.tennis.mapper.ScoreDetailMapper;
import com.sd.tennis.model.ScoreDetail;
import com.sd.tennis.service.ScoreDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
public class ScoreDetailController {
    @Autowired
    private ScoreDetailService scoreDetailService;

    @PostMapping
    @PreAuthorize("hasAuthority('REFEREE')")
    public ResponseEntity<ScoreDetailResponseDTO> addScoreDetail(@RequestBody ScoreDetailDTO dto) {
        var created = scoreDetailService.addScoreDetail(dto);
        return ResponseEntity.ok(ScoreDetailMapper.toScoreDetailResponseDTO(created));
    }

    @PutMapping("/{scoreDetailId}")
    @PreAuthorize("hasAuthority('REFEREE')")
    public ResponseEntity<ScoreDetailResponseDTO> updateScoreDetail(@PathVariable Integer scoreDetailId,
                                                                    @RequestBody ScoreDetailDTO dto) {
        var updated = scoreDetailService.updateScoreDetail(scoreDetailId, dto);
        return ResponseEntity.ok(ScoreDetailMapper.toScoreDetailResponseDTO(updated));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('REFEREE','ADMIN')")
    public ResponseEntity<List<ScoreDetailResponseDTO>> getAllScoreDetails() {
        var list = scoreDetailService.getAllScoreDetails().stream()
                .map(ScoreDetailMapper::toScoreDetailResponseDTO)
                .toList();
        return ResponseEntity.ok(list);
    }
}
