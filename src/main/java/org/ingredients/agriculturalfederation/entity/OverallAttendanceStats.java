package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode

public class OverallAttendanceStats {
    private String collectivityId;
    private long totalMembers;
    private long totalActivities;
    private long totalAttendedActivities;

    public double getOverallAttendanceRate() {
        return totalActivities == 0 ? 0.0 : (double) totalAttendedActivities / (totalMembers * totalActivities) * 100.0;
    }
}
