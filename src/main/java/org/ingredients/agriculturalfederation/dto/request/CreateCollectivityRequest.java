package org.ingredients.agriculturalfederation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CreateCollectivityRequest {
    private String location;
    private String name;
    private Integer number;
    private List<String> members;
    private Boolean federationApproval;
    private CreateCollectivityStructureRequest structure;
}
