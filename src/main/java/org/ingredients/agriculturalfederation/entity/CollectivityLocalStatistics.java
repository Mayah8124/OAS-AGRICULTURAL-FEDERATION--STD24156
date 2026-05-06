package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityLocalStatistics {
    private MemberDescription memberDescription;
    private double earnedAmount;
    private double unpaidAmount;
    private double attendanceRate;
}
