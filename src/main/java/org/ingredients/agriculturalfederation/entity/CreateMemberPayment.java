package org.ingredients.agriculturalfederation.entity;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateMemberPayment {
    private BigDecimal amount;
    private String membershipFeeIdentifier;
    private String accountCreditedIdentifier;
    private PaymentMode paymentMode;
}
