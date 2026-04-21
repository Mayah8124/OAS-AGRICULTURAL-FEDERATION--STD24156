package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Collectivity {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<Member> members;
}
