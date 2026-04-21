package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.CreateMember;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.validator.MemberValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberValidator memberValidator;

    public MemberService(MemberValidator memberValidator) {
        this.memberValidator = memberValidator;
    }

    public List<Member> createMembers(List<CreateMember> request) {
        if (request == null) {
            return List.of();
        }

        List<Member> out = new ArrayList<>();
        for (CreateMember m : request) {
            memberValidator.validateMember(m);

            Member created = new Member();
            created.setId("generated");
            created.setFirstName(m.getFirstName());
            created.setLastName(m.getLastName());
            created.setBirthDate(m.getBirthDate());
            created.setGender(m.getGender());
            created.setAddress(m.getAddress());
            created.setProfession(m.getProfession());
            created.setPhoneNumber(m.getPhoneNumber());
            created.setEmail(m.getEmail());
            created.setOccupation(m.getOccupation());
            created.setReferees(List.of());
            out.add(created);
        }

        return out;
    }
}
