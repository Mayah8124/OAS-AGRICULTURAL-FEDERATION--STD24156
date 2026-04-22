package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.account.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private BigDecimal amount;
    private PaymentMode paymentMode;
    private Object accountCredited;
    private Member memberDebited;
}
