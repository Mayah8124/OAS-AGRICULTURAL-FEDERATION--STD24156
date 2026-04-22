package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityTransactionResponse {
    private String id;
    private LocalDate creationDate;
    private BigDecimal amount;
    private String paymentMode;
    private AccountCreditedResponse accountCredited;
    private MemberDebitedResponse memberDebited;
}
