package org.ingredients.agriculturalfederation.entity;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateCollectivity {
    private String location;
    private List<String> members;
    private Boolean federationApproval;
    private CreateCollectivityStructure structure;
}
