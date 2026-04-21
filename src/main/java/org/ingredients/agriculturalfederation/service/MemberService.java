package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.CreateMember;
import org.ingredients.agriculturalfederation.entity.Member;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class MemberService {

    public List<Member> createMembers(List<CreateMember> request) {
        if (request == null) {
            return List.of();
        }

        List<Member> out = new ArrayList<>();
        for (CreateMember m : request) {
            validateMemberCreation(m);

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

    private static void validateMemberCreation(CreateMember m) {
        if (m == null) {
            throw new RuntimeException("Request body is required");
        }

        if (m.getCollectivityIdentifier() == null || m.getCollectivityIdentifier().isBlank()) {
            throw new RuntimeException("Either collectivity or member not found.");
        }

        if (m.getRegistrationFeePaid() == null || !m.getRegistrationFeePaid() ||
                m.getMembershipDuesPaid() == null || !m.getMembershipDuesPaid()) {
            throw new RuntimeException("Membership dues not paid or registration fee not paid.");
        }

        List<String> referees = m.getReferees();
        if (referees == null || referees.size() < 2) {
            throw new RuntimeException("Member with bad referees or without proper payment.");
        }
        for (String ref : referees) {
            if (ref == null || ref.isBlank()) {
                throw new RuntimeException("Member with bad referees or without proper payment.");
            }
        }
        HashSet<String> distinct = new HashSet<>(referees);
        if (distinct.size() != referees.size()) {
            throw new RuntimeException("Member with bad referees or without proper payment.");
        }
    }
}
