package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountCreditedResponse {
    private String id;
    private BigDecimal amount;
}
