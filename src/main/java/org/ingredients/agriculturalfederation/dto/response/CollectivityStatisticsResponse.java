package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityStatisticsResponse {
    private LocalDate from;
    private LocalDate to;
    private Double attendanceRate;
    private Integer activeMembersCount;
}
