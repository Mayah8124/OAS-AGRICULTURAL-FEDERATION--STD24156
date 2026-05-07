package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateActivityMemberAttendance;
import org.ingredients.agriculturalfederation.dto.response.ActivityMemberAttendance;
import org.ingredients.agriculturalfederation.repository.ActivityAttendanceRepository;
import org.ingredients.agriculturalfederation.validator.ActivityAttendanceValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityAttendanceService {

    private final ActivityAttendanceRepository activityAttendanceRepository;
    private final ActivityAttendanceValidator activityAttendanceValidator;

    public ActivityAttendanceService(ActivityAttendanceRepository activityAttendanceRepository, ActivityAttendanceValidator activityAttendanceValidator) {
        this.activityAttendanceRepository = activityAttendanceRepository;
        this.activityAttendanceValidator = activityAttendanceValidator;
    }

    public List<ActivityMemberAttendance> getActivityAttendance(String collectivityId, String activityId) {
        activityAttendanceValidator.validateGetActivityAttendance(collectivityId, activityId);
        
        return activityAttendanceRepository.getActivityAttendance(collectivityId, activityId);
    }

    public List<ActivityMemberAttendance> createAttendance(String collectivityId, String activityId, List<CreateActivityMemberAttendance> requests) {
        activityAttendanceValidator.validateCreateAttendance(collectivityId, activityId, requests);
        
        return activityAttendanceRepository.createAttendance(collectivityId, activityId, requests);
    }
}
