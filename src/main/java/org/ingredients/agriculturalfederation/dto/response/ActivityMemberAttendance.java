package org.ingredients.agriculturalfederation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ingredients.agriculturalfederation.entity.AttendanceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityMemberAttendance {
    private String id;
    private MemberDescription memberDescription;
    private AttendanceStatus attendanceStatus;
}
