package org.ingredients.agriculturalfederation.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class MemberDebitedResponse {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String profession;
    private String phoneNumber;
    private String email;
    private String occupation;
    private String id;
    private List<String> referees;
}
