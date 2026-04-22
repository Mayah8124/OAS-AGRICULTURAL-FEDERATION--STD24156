package org.ingredients.agriculturalfederation.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class CashAccount extends FinancialAccount {
    private BigDecimal amount;
}
