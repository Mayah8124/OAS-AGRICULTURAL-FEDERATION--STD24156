package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.enums.ActivityType;
import org.ingredients.agriculturalfederation.entity.enums.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityActivity {
    private String id;
    private String label;
    private ActivityType activityType;
    private List<MemberOccupation> memberOccupationConcerned;
    private MonthlyRecurrenceRule recurrenceRule;
    private LocalDate executiveDate;
}
