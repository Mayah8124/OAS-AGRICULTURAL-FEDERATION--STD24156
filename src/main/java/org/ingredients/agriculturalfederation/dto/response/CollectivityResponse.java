package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class CollectivityResponse {
    private String id;
    private String name;
    private Integer number;
    private String location;
    private CollectivityStructureResponse structure;
    private List<MemberResponse> members;
}
