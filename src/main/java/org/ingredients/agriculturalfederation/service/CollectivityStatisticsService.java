package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.*;
import org.ingredients.agriculturalfederation.repository.CollectivityActivityAttendanceRepository;
import org.ingredients.agriculturalfederation.repository.CollectivityRepository;
import org.ingredients.agriculturalfederation.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CollectivityStatisticsService {

    private final CollectivityRepository collectivityRepository;
    private final CollectivityActivityAttendanceRepository activityAttendanceRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public CollectivityStatisticsService(
            CollectivityRepository collectivityRepository,
            CollectivityActivityAttendanceRepository activityAttendanceRepository,
            MemberPaymentRepository memberPaymentRepository
    ) {
        this.collectivityRepository = collectivityRepository;
        this.activityAttendanceRepository = activityAttendanceRepository;
        this.memberPaymentRepository = memberPaymentRepository;
    }

    public List<CollectivityOverallStatistics> getCollectivitiesStatistics(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Invalid date range");
        }

        List<Collectivity> collectivities = collectivityRepository.findAll();
        List<OverallAttendanceStats> overallAttendanceStats = activityAttendanceRepository.getOverallAttendanceStats(from, to);
        List<CollectivityPaymentStats> paymentStats = memberPaymentRepository.getCollectivityPaymentStats(from, to);
        
        List<CollectivityOverallStatistics> statistics = new ArrayList<>();
        
        for (Collectivity collectivity : collectivities) {
            Optional<OverallAttendanceStats> attendanceStat = overallAttendanceStats.stream()
                    .filter(stat -> stat.getCollectivityId().equals(collectivity.getId()))
                    .findFirst();
            
            Optional<CollectivityPaymentStats> paymentStat = paymentStats.stream()
                    .filter(stat -> stat.getCollectivityId().equals(collectivity.getId()))
                    .findFirst();
            
            double overallAttendanceRate = attendanceStat
                    .map(OverallAttendanceStats::getOverallAttendanceRate)
                    .orElse(0.0);
            
            double duePercentage = paymentStat
                    .map(CollectivityPaymentStats::getDuePercentage)
                    .orElse(0.0);
            
            long newMembersCount = paymentStat
                    .map(CollectivityPaymentStats::getNewMembersCount)
                    .orElse(0L);
            
            CollectivityOverallStatistics stat = CollectivityOverallStatistics.builder()
                    .collectivityInformation(new CollectivityInformation(
                            collectivity.getName(),
                            collectivity.getNumber()
                    ))
                    .newMembersNumber((int) newMembersCount)
                    .overallMemberCurrentDuePercentage(duePercentage)
                    .overallMemberAssiduityPercentage(overallAttendanceRate)
                    .build();
            
            statistics.add(stat);
        }
        
        return statistics;
    }
}
