package org.ingredients.agriculturalfederation.entity;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateCollectivityStructure {
    private String president;
    private String vicePresident;
    private String treasurer;
    private String secretary;
}