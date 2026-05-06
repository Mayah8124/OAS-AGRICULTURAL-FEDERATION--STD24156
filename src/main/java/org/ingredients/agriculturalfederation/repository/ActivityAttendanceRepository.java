package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.dto.request.CreateActivityMemberAttendance;
import org.ingredients.agriculturalfederation.dto.response.ActivityMemberAttendance;

import java.util.List;

public interface ActivityAttendanceRepository {
    boolean activityExists(String activityId);
    boolean collectivityExists(String collectivityId);
    
    List<ActivityMemberAttendance> getActivityAttendance(String collectivityId, String activityId);
    List<ActivityMemberAttendance> createAttendance(String collectivityId, String activityId, List<CreateActivityMemberAttendance> requests);
}
