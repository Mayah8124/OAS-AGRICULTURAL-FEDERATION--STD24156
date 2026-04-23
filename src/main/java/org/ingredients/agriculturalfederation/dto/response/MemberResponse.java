package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;
import org.ingredients.agriculturalfederation.entity.Gender;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberResponse {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Gender gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private MemberOccupation occupation;
    private List<MemberResponse> referees;
}
