package org.ingredients.agriculturalfederation.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateMemberShipFee {
    private LocalDate eligibleFrom;
    private Frequency frequency;
    private BigDecimal amount;
    private String label;
}
