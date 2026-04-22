package org.ingredients.agriculturalfederation.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberPayment {
    private String id;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private LocalDate creationDate;
}
