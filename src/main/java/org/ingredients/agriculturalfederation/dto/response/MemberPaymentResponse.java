package org.ingredients.agriculturalfederation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberPaymentResponse {
    private String id;
    private BigDecimal amount;
    private String paymentMode;
    private AccountCreditedResponse accountCredited;
    private LocalDate creationDate;
}
