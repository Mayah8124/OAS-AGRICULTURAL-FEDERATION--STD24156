package org.ingredients.agriculturalfederation.entity.account;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class FinancialAccount {
    private String id;
    private BigDecimal amount;
}
