package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;

import java.time.LocalDate;
import java.util.List;

public interface MemberPaymentRepository {
    List<MemberPaymentResponse> createPayments(String memberId, String collectivityId, List<CreateMemberPaymentRequest> requests);
    List<MemberPaymentResponse> findByMemberIdAndDateRange(String memberId, LocalDate from, LocalDate to);
}
