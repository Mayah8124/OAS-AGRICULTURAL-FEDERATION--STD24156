package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityActivityRequest;
import org.ingredients.agriculturalfederation.dto.request.MonthlyRecurrenceRuleRequest;
import org.ingredients.agriculturalfederation.repository.CollectivityActivityRepository;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class CollectivityActivityValidator {

    private static final List<String> ACTIVITY_TYPES = Arrays.asList("MEETING", "TRAINING", "OTHER");
    private static final List<String> DAYS_OF_WEEK = Arrays.asList("MO", "TU", "WE", "TH", "FR", "SA", "SU");

    private final CollectivityActivityRepository collectivityActivityRepository;

    public CollectivityActivityValidator(CollectivityActivityRepository collectivityActivityRepository) {
        this.collectivityActivityRepository = collectivityActivityRepository;
    }

    public void validateAddActivities(String collectivityId, List<CreateCollectivityActivityRequest> requests) {
        if (collectivityId == null || collectivityId.trim().isEmpty()) {
            throw new InvalidCollectivityException("Collectivity identifier is required");
        }

        if (requests == null) {
            throw new InvalidCollectivityException("Request body is required");
        }

        if (!collectivityActivityRepository.collectivityExists(collectivityId)) {
            throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
        }

        for (CreateCollectivityActivityRequest req : requests) {
            if (req == null) {
                throw new InvalidCollectivityException("Activity cannot be null");
            }

            if (req.getLabel() == null || req.getLabel().trim().isEmpty()) {
                throw new InvalidCollectivityException("Activity label is required");
            }

            if (req.getActivityType() == null || req.getActivityType().trim().isEmpty()) {
                throw new InvalidCollectivityException("Activity type is required");
            }

            if (!ACTIVITY_TYPES.contains(req.getActivityType())) {
                throw new InvalidCollectivityException("Activity type must be one of: MEETING, TRAINING, OTHER");
            }

            if (req.getExecutiveDate() != null && req.getRecurrenceRule() != null) {
                throw new InvalidCollectivityException("Either executive date or recurrence rule can be provided at same time, not both.");
            }

            MonthlyRecurrenceRuleRequest recurrence = req.getRecurrenceRule();
            if (recurrence != null) {
                if (recurrence.getWeekOrdinal() == null || recurrence.getWeekOrdinal() < 1 || recurrence.getWeekOrdinal() > 5) {
                    throw new InvalidCollectivityException("Recurrence rule weekOrdinal must be between 1 and 5");
                }
                if (recurrence.getDayOfWeek() == null || recurrence.getDayOfWeek().trim().isEmpty()) {
                    throw new InvalidCollectivityException("Recurrence rule dayOfWeek is required");
                }

                if (!DAYS_OF_WEEK.contains(recurrence.getDayOfWeek())) {
                    throw new InvalidCollectivityException("Recurrence rule dayOfWeek must be one of: MO, TU, WE, TH, FR, SA, SU");
                }
            }
        }
    }
}
