package org.ingredients.agriculturalfederation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ingredients.agriculturalfederation.entity.AttendanceStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateActivityMemberAttendance {
    private String memberIdentifier;
    private AttendanceStatus attendanceStatus;
}
