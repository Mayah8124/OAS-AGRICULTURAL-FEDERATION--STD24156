package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityStructureRequest;
import org.ingredients.agriculturalfederation.entity.*;
import org.ingredients.agriculturalfederation.repository.CollectivityRepository;
import org.ingredients.agriculturalfederation.repository.MemberRepository;
import org.ingredients.agriculturalfederation.repository.MembershipFeeRepository;
import org.ingredients.agriculturalfederation.repository.MemberPaymentRepository;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.validator.CollectivityValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    private final CollectivityValidator collectivityValidator;
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;
    private final MembershipFeeRepository membershipFeeRepository;
    private final MemberPaymentRepository memberPaymentRepository;

    public CollectivityService(CollectivityValidator collectivityValidator, CollectivityRepository collectivityRepository, MemberRepository memberRepository, MembershipFeeRepository membershipFeeRepository, MemberPaymentRepository memberPaymentRepository) {
        this.collectivityValidator = collectivityValidator;
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
        this.membershipFeeRepository = membershipFeeRepository;
        this.memberPaymentRepository = memberPaymentRepository;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivityRequest> requests) {
        if (requests == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivityRequest req : requests) {
            collectivityValidator.validateCollectivity(req);

            Collectivity collectivity = new Collectivity();
            collectivity.setId(UUID.randomUUID().toString());
            collectivity.setLocation(req.getLocation());
            collectivity.setName(req.getName());
            collectivity.setNumber(req.getNumber());
            collectivity.setStructure(toEntityStructure(req.getStructure()));
            collectivity.setMembers(new ArrayList<>());

            collectivityRepository.save(collectivity, false);
            out.add(collectivity);
        }
        return out;
    }

    public Collectivity updateInformations(String id, CollectivityInformationRequest request) {
        collectivityRepository.assignIdentity(id, request.getName(), request.getNumber());
        return collectivityRepository.findById(id).orElseThrow();
    }

    private CollectivityStructure toEntityStructure(CreateCollectivityStructureRequest structure) {
        if (structure == null) {
            return null;
        }

        Member president = memberRepository.findById(structure.getPresident()).orElse(null);
        Member vicePresident = memberRepository.findById(structure.getVicePresident()).orElse(null);
        Member treasurer = memberRepository.findById(structure.getTreasurer()).orElse(null);
        Member secretary = memberRepository.findById(structure.getSecretary()).orElse(null);

        return new CollectivityStructure(president, vicePresident, treasurer, secretary);
    }

    public Collectivity getCollectivityById(String id) {
        return collectivityRepository.findByIdWithMembers(id).get();
    }

    public List<CollectivityLocalStatistics> getCollectivityStatistics(String id, LocalDate from, LocalDate to) {
        Collectivity collectivity = collectivityRepository.findByIdWithMembers(id)
                .orElseThrow(() -> new IllegalArgumentException("Collectivity not found"));
        
        List<MembershipFee> activeFees = membershipFeeRepository.findActiveByCollectivityId(id);
        
        List<CollectivityLocalStatistics> statistics = new ArrayList<>();
        
        for (Member member : collectivity.getMembers()) {
            MemberDescription memberDescription = new MemberDescription();
            memberDescription.setId(member.getId());
            memberDescription.setFirstName(member.getFirstName());
            memberDescription.setLastName(member.getLastName());
            memberDescription.setEmail(member.getEmail());
            
            List<MemberPaymentResponse> payments = memberPaymentRepository.findByMemberIdAndDateRange(member.getId(), from, to);
            
            double earnedAmount = payments.stream()
                    .mapToDouble(payment -> payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0)
                    .sum();
            
            double unpaidAmount = 0.0;
            for (MembershipFee fee : activeFees) {
                if (fee.getEligibleFrom() != null && !fee.getEligibleFrom().isAfter(to)) {
                    LocalDate feeStartDate = fee.getEligibleFrom().isBefore(from) ? from : fee.getEligibleFrom();
                    double feeAmount = fee.getAmount() != null ? fee.getAmount().doubleValue() : 0.0;
                    long periods = calculatePeriods(feeStartDate, to, fee.getFrequency());
                    double expectedAmount = periods * feeAmount;
                    unpaidAmount += expectedAmount;
                }
            }
            
            unpaidAmount = Math.max(0.0, unpaidAmount - earnedAmount);
            
            CollectivityLocalStatistics stat = new CollectivityLocalStatistics();
            stat.setMemberDescription(memberDescription);
            stat.setEarnedAmount(earnedAmount);
            stat.setUnpaidAmount(unpaidAmount);
            
            statistics.add(stat);
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
