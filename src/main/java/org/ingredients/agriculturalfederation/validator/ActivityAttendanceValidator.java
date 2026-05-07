package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.dto.request.CreateActivityMemberAttendance;
import org.ingredients.agriculturalfederation.entity.AttendanceStatus;
import org.ingredients.agriculturalfederation.repository.ActivityAttendanceRepository;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityAttendanceValidator {

    private final ActivityAttendanceRepository activityAttendanceRepository;

    public ActivityAttendanceValidator(ActivityAttendanceRepository activityAttendanceRepository) {
        this.activityAttendanceRepository = activityAttendanceRepository;
    }

    public void validateCollectivityExists(String collectivityId) {
        if (collectivityId == null || collectivityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Collectivity identifier is required");
        }

        if (!activityAttendanceRepository.collectivityExists(collectivityId)) {
            throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
        }
    }

    public void validateActivityExists(String activityId) {
        if (activityId == null || activityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity identifier is required");
        }

        if (!activityAttendanceRepository.activityExists(activityId)) {
            throw new IllegalArgumentException("Activity with ID " + activityId + " not found");
        }
    }

    public void validateGetActivityAttendance(String collectivityId, String activityId) {
        validateCollectivityExists(collectivityId);
        validateActivityExists(activityId);
    }

    public void validateCreateAttendance(String collectivityId, String activityId, List<CreateActivityMemberAttendance> requests) {
        validateCollectivityExists(collectivityId);
        validateActivityExists(activityId);

        if (requests == null) {
            throw new IllegalArgumentException("Request body is required");
        }

        for (CreateActivityMemberAttendance request : requests) {
            if (request == null) {
                throw new IllegalArgumentException("Attendance request cannot be null");
            }

            if (request.getMemberIdentifier() == null || request.getMemberIdentifier().trim().isEmpty()) {
                throw new IllegalArgumentException("Member identifier is required");
            }

            if (request.getAttendanceStatus() == null) {
                throw new IllegalArgumentException("Attendance status is required");
            }

            if (request.getAttendanceStatus() != AttendanceStatus.ATTENDED &&
                request.getAttendanceStatus() != AttendanceStatus.MISSING) {
                throw new IllegalArgumentException("Attendance status must be ATTENDED or MISSING for creation");
            }
        }
    }
}
