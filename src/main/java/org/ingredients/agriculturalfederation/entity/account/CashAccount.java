package org.ingredients.agriculturalfederation.entity.account;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CashAccount {
    private String id;
    private Integer amount;
}
