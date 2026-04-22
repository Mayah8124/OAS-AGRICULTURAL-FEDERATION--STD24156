package org.ingredients.agriculturalfederation.entity.account;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.MobileBankingService;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MobileBankingAccount {
    private String id;
    private String holderName;
    private MobileBankingService mobileBankingService;
    private Integer mobileNumber;
    private BigDecimal amount;
}
