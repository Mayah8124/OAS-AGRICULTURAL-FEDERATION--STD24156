package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class MobileBankingAccount extends FinancialAccount {
    private String holderName;
    private MobileBankingService mobileBankingService;
    private String mobileNumber;
    private Double amount;
}
