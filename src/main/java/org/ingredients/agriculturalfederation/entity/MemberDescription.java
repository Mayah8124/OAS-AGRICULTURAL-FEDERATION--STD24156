package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberDescription {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
}
