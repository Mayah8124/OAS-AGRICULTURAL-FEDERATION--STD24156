package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.response.CollectivityStatisticsResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class CollectivityStatisticsService {

    public CollectivityStatisticsResponse getCollectivitiesStatistics(LocalDate from, LocalDate to) {
        LocalDate resolvedFrom = from == null ? LocalDate.now().minusMonths(1) : from;
        LocalDate resolvedTo = to == null ? LocalDate.now() : to;

        if (resolvedFrom.isAfter(resolvedTo)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        double attendanceRate = 0.0;
        int activeMembersCount = 0;

        return new CollectivityStatisticsResponse(resolvedFrom, resolvedTo, attendanceRate, activeMembersCount);
    }
}
