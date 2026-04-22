package org.ingredients.agriculturalfederation.entity.account;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.Bank;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class BankAccount {
    private String id;
    private String holderName;
    private Bank bankName;
    private Integer bankCode;
    private Integer bankBranchCode;
    private Integer bankAccountNumber;
    private Integer bankAccountKey;
    private BigDecimal amount;
}
