package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityActivityRequest;
import org.ingredients.agriculturalfederation.dto.request.MonthlyRecurrenceRuleRequest;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.entity.MonthlyRecurrenceRule;
import org.ingredients.agriculturalfederation.repository.CollectivityActivityRepository;
import org.ingredients.agriculturalfederation.validator.CollectivityActivityValidator;
import org.springframework.stereotype.Service;

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
            MonthlyRecurrenceRule mappedRule = rr == null ? null : MonthlyRecurrenceRule.builder()
                    .weekOrdinal(rr.getWeekOrdinal())
                    .dayOfWeek(rr.getDayOfWeek())
                    .build();

            return CollectivityActivity.builder()
                    .label(req.getLabel())
                    .activityType(req.getActivityType())
                    .memberOccupationConcerned(req.getMemberOccupationConcerned())
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
}
