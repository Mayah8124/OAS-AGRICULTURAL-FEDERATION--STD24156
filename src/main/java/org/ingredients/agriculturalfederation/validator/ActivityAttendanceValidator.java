package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.repository.ActivityAttendanceRepository;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Component;

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
}
