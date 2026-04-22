package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MembershipFee extends CreateMemberShipFee {
    private String id;
    private ActivityStatus status;
    private String collectivityId;
}
