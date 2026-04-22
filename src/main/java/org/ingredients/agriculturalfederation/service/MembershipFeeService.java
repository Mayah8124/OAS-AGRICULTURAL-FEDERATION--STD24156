package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.dto.response.MembershipFeeResponse;
import org.ingredients.agriculturalfederation.entity.ActivityStatus;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.repository.MembershipFeeRepository;
import org.ingredients.agriculturalfederation.validator.MembershipFeeValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembershipFeeService {
    private final MembershipFeeValidator membershipFeeValidator;
    private final MembershipFeeRepository membershipFeeRepository;

    public MembershipFeeService(MembershipFeeValidator membershipFeeValidator, MembershipFeeRepository membershipFeeRepository) {
        this.membershipFeeValidator = membershipFeeValidator;
        this.membershipFeeRepository = membershipFeeRepository;
    }

    public List<MembershipFee> createMembershipFees(String collectivityId, List<CreateMembershipFeeRequest> requests) {
        membershipFeeValidator.validateCreateRequests(collectivityId, requests);

        List<MembershipFee> toSave = requests.stream().map(req -> MembershipFee.builder()
                .eligibleFrom(req.getEligibleFrom())
                .frequency(req.getFrequency())
                .amount(req.getAmount())
                .label(req.getLabel())
                .status(ActivityStatus.ACTIVE)
                .build()).collect(Collectors.toList());

        return membershipFeeRepository.saveAll(collectivityId, toSave);
    }

    public List<MembershipFeeResponse> getMembershipFees(String collectivityId) {
        membershipFeeValidator.validateCollectivityId(collectivityId);

        List<MembershipFee> membershipFees = membershipFeeRepository.findByCollectivityId(collectivityId);

        return membershipFees.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MembershipFeeResponse convertToDto(MembershipFee membershipFee) {
        return MembershipFeeResponse.builder()
                .id(membershipFee.getId())
                .eligibleFrom(membershipFee.getEligibleFrom())
                .frequency(membershipFee.getFrequency().name())
                .amount(membershipFee.getAmount())
                .label(membershipFee.getLabel())
                .status(membershipFee.getStatus().name())
                .build();
    }
}
