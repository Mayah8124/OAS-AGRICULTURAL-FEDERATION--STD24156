package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MembershipFee {
    private String id;
    private java.time.LocalDate eligibleFrom;
    private Frequency frequency;
    private java.math.BigDecimal amount;
    private String label;
    private ActivityStatus status;
    private String collectivityId;
}
