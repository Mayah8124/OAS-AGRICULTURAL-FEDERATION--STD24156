package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class Member extends MemberInformation {
    private String id;
    private List<Member> referees;
}
