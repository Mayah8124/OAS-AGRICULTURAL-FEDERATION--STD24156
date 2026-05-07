package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityPaymentStats {
    private String collectivityId;
    private long totalMembers;
    private long membersCurrentWithDues;
    private long newMembersCount;

    public double getDuePercentage() {
        return totalMembers == 0 ? 0.0 : (double) membersCurrentWithDues / totalMembers * 100.0;
    }
}
