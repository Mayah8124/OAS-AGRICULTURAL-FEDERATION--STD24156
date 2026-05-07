package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.enums.AttendanceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class ActivityMemberAttendance {
    private String id;
    private MemberDescription memberDescription;
    private AttendanceStatus attendanceStatus;
}
