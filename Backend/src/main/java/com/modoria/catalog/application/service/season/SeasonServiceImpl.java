package com.modoria.catalog.application.service.season;

import com.modoria.catalog.domain.model.Season;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;

@Service
public class SeasonServiceImpl implements SeasonService {

    @Override
    public Season getCurrentSeason() {
        Month month = LocalDate.now().getMonth();
        return switch (month) {
            case MARCH, APRIL, MAY -> Season.SPRING;
            case JUNE, JULY, AUGUST -> Season.SUMMER;
            case SEPTEMBER, OCTOBER, NOVEMBER -> Season.AUTUMN;
            case DECEMBER, JANUARY, FEBRUARY -> Season.WINTER;
        };
    }
}
