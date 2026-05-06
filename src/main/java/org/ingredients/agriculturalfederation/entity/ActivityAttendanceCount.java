package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class ActivityAttendanceCount {
    private String memberId;
    private long totalActivities;
    private long attendedActivities;

    public ActivityAttendanceCount(String memberId, long totalActivities, long attendedActivities) {
        this.memberId = memberId;
        this.totalActivities = totalActivities;
        this.attendedActivities = attendedActivities;
    }

    public String getMemberId() { return memberId; }
    public long getTotalActivities() { return totalActivities; }
    public long getAttendedActivities() { return attendedActivities; }

    public double getAttendanceRate() {
        return totalActivities == 0 ? 0.0 : (double) attendedActivities / totalActivities * 100.0;
    }
}
