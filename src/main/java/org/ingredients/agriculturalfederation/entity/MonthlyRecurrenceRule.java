package org.ingredients.agriculturalfederation.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MonthlyRecurrenceRule {
    private Integer weekOrdinal;
    private String dayOfWeek;
}
