package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.*;
import org.ingredients.agriculturalfederation.repository.*;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class CollectivityMemberStatisticsService {

    private final CollectivityRepository collectivityRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberPaymentRepository memberPaymentRepository;
    private final CollectivityActivityAttendanceRepository activityAttendanceRepository;

    public CollectivityMemberStatisticsService(
            CollectivityRepository collectivityRepository,
            MembershipFeeRepository membershipFeeRepository,
            MemberPaymentRepository memberPaymentRepository,
            CollectivityActivityAttendanceRepository activityAttendanceRepository
    ) {
        this.collectivityRepository = collectivityRepository;
        this.membershipFeeRepository = membershipFeeRepository;
        this.memberPaymentRepository = memberPaymentRepository;
        this.activityAttendanceRepository = activityAttendanceRepository;
    }

    public List<CollectivityLocalStatistics> getCollectivityStatistics(String id, LocalDate from, LocalDate to) {
        if (!collectivityRepository.findById(id).isPresent()) {
            throw new CollectivityNotFoundException("Collectivity not found");
        }

        List<MemberFullStats> memberFullStats = activityAttendanceRepository.getMemberFullStats(id, from, to);
        
        List<CollectivityLocalStatistics> statistics = new ArrayList<>();

        for (MemberFullStats memberStat : memberFullStats) {
            MemberDescription desc = new MemberDescription(
                memberStat.getMemberId(), 
                memberStat.getFirstName(), 
                memberStat.getLastName(), 
                memberStat.getEmail()
            );
            
            statistics.add(new CollectivityLocalStatistics(
                desc, 
                memberStat.getEarned(), 
                memberStat.getUnpaid(), 
                memberStat.getAttendanceRate()
            ));
        }

        return statistics;
    }

    private long calculatePeriods(LocalDate startDate, LocalDate endDate, Frequency frequency) {
        switch (frequency) {
            case WEEKLY:
                return ChronoUnit.WEEKS.between(startDate, endDate) + 1;
            case MONTHLY:
                return ChronoUnit.MONTHS.between(startDate, endDate) + 1;
            case ANNUALLY:
                return ChronoUnit.YEARS.between(startDate, endDate) + 1;
            case PUNCTUALLY:
                return 1;
            default:
                return 0;
        }
    }
}
