package org.ingredients.agriculturalfederation.entity;

import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode

public class MemberFullStats {
    private String memberId;
    private String firstName;
    private String lastName;
    private String email;
    private double earned;
    private double unpaid;
    private double attendanceRate;

}
