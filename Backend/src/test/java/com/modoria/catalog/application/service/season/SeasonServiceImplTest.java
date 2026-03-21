package com.modoria.catalog.application.service.season;

import com.modoria.catalog.domain.model.Season;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SeasonServiceImplTest {

    private final SeasonServiceImpl service = new SeasonServiceImpl();

    @Test
    void getCurrentSeason_returnsNonNullSeason() {
        Season season = service.getCurrentSeason();

        assertThat(season).isNotNull();
        assertThat(Season.values()).contains(season);
    }
}
