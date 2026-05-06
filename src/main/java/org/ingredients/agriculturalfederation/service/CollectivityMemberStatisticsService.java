package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.entity.*;
import org.ingredients.agriculturalfederation.repository.*;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
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
        Collectivity collectivity = collectivityRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity not found"));

        List<MembershipFee> activeFees = membershipFeeRepository.findActiveByCollectivityId(id);
        List<CollectivityLocalStatistics> statistics = new ArrayList<>();

        List<ActivityAttendanceCount> attendanceStats = activityAttendanceRepository.getMemberAttendanceStats(id, from, to);

        for (Member member : collectivity.getMembers()) {
            MemberDescription desc = new MemberDescription(member.getId(), member.getFirstName(), member.getLastName(), member.getEmail());
            double earned = memberPaymentRepository.findTotalEarnedByMemberIdAndDateRange(member.getId(), from, to);
            double unpaid = 0.0;

            for (MembershipFee fee : activeFees) {
                if (fee.getEligibleFrom() != null && !fee.getEligibleFrom().isAfter(to)) {
                    LocalDate start = fee.getEligibleFrom().isBefore(from) ? from : fee.getEligibleFrom();
                    unpaid += calculatePeriods(start, to, fee.getFrequency()) * (fee.getAmount() != null ? fee.getAmount().doubleValue() : 0.0);
                }
            }

            double attendanceRate = attendanceStats.stream()
                    .filter(stat -> stat.getMemberId().equals(member.getId()))
                    .findFirst()
                    .map(ActivityAttendanceCount::getAttendanceRate)
                    .orElse(0.0);

            statistics.add(new CollectivityLocalStatistics(desc, earned, Math.max(0.0, unpaid - earned), attendanceRate));
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
