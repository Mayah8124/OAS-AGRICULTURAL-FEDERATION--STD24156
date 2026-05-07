package org.ingredients.agriculturalfederation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateCollectivityActivityRequest {
    private String label;
    private String activityType;
    private List<MemberOccupation> memberOccupationConcerned;
    private MonthlyRecurrenceRuleRequest recurrenceRule;
    private LocalDate executiveDate;
}
