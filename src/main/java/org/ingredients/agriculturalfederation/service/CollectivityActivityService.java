package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityActivityRequest;
import org.ingredients.agriculturalfederation.dto.request.MonthlyRecurrenceRuleRequest;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.entity.MonthlyRecurrenceRule;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.ingredients.agriculturalfederation.entity.enums.ActivityType;
import org.ingredients.agriculturalfederation.entity.enums.DayOfWeek;
import org.ingredients.agriculturalfederation.repository.CollectivityActivityRepository;
import org.ingredients.agriculturalfederation.validator.CollectivityActivityValidator;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CollectivityActivityService {

    private final CollectivityActivityValidator collectivityActivityValidator;
    private final CollectivityActivityRepository collectivityActivityRepository;

    public CollectivityActivityService(CollectivityActivityValidator collectivityActivityValidator, CollectivityActivityRepository collectivityActivityRepository) {
        this.collectivityActivityValidator = collectivityActivityValidator;
        this.collectivityActivityRepository = collectivityActivityRepository;
    }

    public List<CollectivityActivity> addActivities(String collectivityId, List<CreateCollectivityActivityRequest> requests) {
        collectivityActivityValidator.validateAddActivities(collectivityId, requests);

        List<CollectivityActivity> toCreate = requests.stream().map(req -> {
            MonthlyRecurrenceRuleRequest rr = req.getRecurrenceRule();
            DayOfWeek mappedDayOfWeek = null;
            if (rr != null && rr.getDayOfWeek() != null) {
                try {
                    mappedDayOfWeek = DayOfWeek.valueOf(rr.getDayOfWeek().trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    mappedDayOfWeek = null;
                }
            }

            MonthlyRecurrenceRule mappedRule = rr == null ? null : MonthlyRecurrenceRule.builder()
                    .weekOrdinal(rr.getWeekOrdinal())
                    .dayOfWeek(mappedDayOfWeek)
                    .build();

            ActivityType mappedActivityType = null;
            if (req.getActivityType() != null) {
                try {
                    mappedActivityType = ActivityType.valueOf(req.getActivityType().trim().toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    mappedActivityType = null;
                }
            }

            List<org.ingredients.agriculturalfederation.entity.enums.MemberOccupation> mappedOccupations = null;
            if (req.getMemberOccupationConcerned() != null) {
                mappedOccupations = req.getMemberOccupationConcerned().stream()
                        .map(CollectivityActivityService::mapMemberOccupation)
                        .collect(Collectors.toList());
            }

            return CollectivityActivity.builder()
                    .label(req.getLabel())
                    .activityType(mappedActivityType)
                    .memberOccupationConcerned(mappedOccupations)
                    .recurrenceRule(mappedRule)
                    .executiveDate(req.getExecutiveDate())
                    .build();
        }).collect(Collectors.toList());

        return collectivityActivityRepository.addActivities(collectivityId, toCreate);
    }

    public List<CollectivityActivity> getActivities(String collectivityId) {
        collectivityActivityValidator.validateCollectivityExists(collectivityId);
        return collectivityActivityRepository.getActivities(collectivityId);
    }

    private static org.ingredients.agriculturalfederation.entity.enums.MemberOccupation mapMemberOccupation(MemberOccupation raw) {
        if (raw == null) {
            return null;
        }
        try {
            return org.ingredients.agriculturalfederation.entity.enums.MemberOccupation.valueOf(raw.name());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
