package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityOverallStatistics {
    private CollectivityInformation collectivityInformation;
    private Integer newMembersNumber;
    private Double overallMemberCurrentDuePercentage;
    private Double overallMemberAssiduityPercentage;
}
