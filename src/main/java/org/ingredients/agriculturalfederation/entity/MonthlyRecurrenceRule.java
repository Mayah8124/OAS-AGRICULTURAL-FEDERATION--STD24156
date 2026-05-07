package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.enums.DayOfWeek;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MonthlyRecurrenceRule {
    private Integer weekOrdinal;
    private DayOfWeek dayOfWeek;
}
