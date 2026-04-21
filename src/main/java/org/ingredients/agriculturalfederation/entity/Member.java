package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Builder
public class Member extends MemberInformation {
    private String id;
    private List<Member> referees;
}
