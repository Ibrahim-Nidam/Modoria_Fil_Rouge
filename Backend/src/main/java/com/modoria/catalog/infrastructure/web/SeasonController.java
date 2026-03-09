package com.modoria.catalog.infrastructure.web;

import com.modoria.catalog.application.service.season.SeasonService;
import com.modoria.catalog.domain.model.Season;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/seasons")
public class SeasonController {

    private final SeasonService seasonService;

    public SeasonController(SeasonService seasonService) {
        this.seasonService = seasonService;
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, String>> getCurrentSeason() {
        Season currentSeason = seasonService.getCurrentSeason();
        return ResponseEntity.ok(Map.of("season", currentSeason.name().toLowerCase()));
    }
}
