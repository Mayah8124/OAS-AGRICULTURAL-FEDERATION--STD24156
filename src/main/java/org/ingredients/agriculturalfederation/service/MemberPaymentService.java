package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.repository.MemberPaymentRepository;
import org.ingredients.agriculturalfederation.validator.MemberPaymentValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberPaymentService {

    private final MemberPaymentValidator memberPaymentValidator;
    private final MemberPaymentRepository memberPaymentRepository;

    public MemberPaymentService(MemberPaymentValidator memberPaymentValidator, MemberPaymentRepository memberPaymentRepository) {
        this.memberPaymentValidator = memberPaymentValidator;
        this.memberPaymentRepository = memberPaymentRepository;
    }

    public List<MemberPaymentResponse> createPayments(String memberId, List<CreateMemberPaymentRequest> requests) {
        memberPaymentValidator.validateCreateRequests(memberId, requests);
        String collectivityId = memberPaymentValidator.getMemberCollectivityId(memberId);
        return memberPaymentRepository.createPayments(memberId, collectivityId, requests);
    }
}
