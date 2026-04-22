package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.ingredients.agriculturalfederation.repository.MembershipFeeRepository;
import org.ingredients.agriculturalfederation.validator.MembershipFeeValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipFeeService {
    private final MembershipFeeValidator membershipFeeValidator;
    private final MembershipFeeRepository membershipFeeRepository;

    public MembershipFeeService(MembershipFeeValidator membershipFeeValidator, MembershipFeeRepository membershipFeeRepository) {
        this.membershipFeeValidator = membershipFeeValidator;
        this.membershipFeeRepository = membershipFeeRepository;
    }

    public List<MembershipFee> getMembershipFees(String collectivityId) {
        membershipFeeValidator.validateCollectivityId(collectivityId);
        return membershipFeeRepository.findByCollectivityId(collectivityId);
    }
}
