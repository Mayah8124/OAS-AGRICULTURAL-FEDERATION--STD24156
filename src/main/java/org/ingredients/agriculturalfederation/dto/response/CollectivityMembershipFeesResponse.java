package org.ingredients.agriculturalfederation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityMembershipFeesResponse {
    private String collectivityId;
    private String collectivityName;
    private List<MembershipFeeResponse> membershipFees;
    private Integer totalMembershipFees;
    private BigDecimal totalAmount;
}
