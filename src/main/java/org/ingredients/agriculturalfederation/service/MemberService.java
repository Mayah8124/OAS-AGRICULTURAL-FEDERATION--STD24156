package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CreateMemberRequest;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.repository.MemberRepository;
import org.ingredients.agriculturalfederation.validator.MemberValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberValidator memberValidator;
    private final MemberRepository memberRepository;

    public MemberService(MemberValidator memberValidator, MemberRepository memberRepository) {
        this.memberValidator = memberValidator;
        this.memberRepository = memberRepository;
    }

    public List<Member> createMembers(List<CreateMemberRequest> request) {
        if (request == null) {
            return List.of();
        }

        List<Member> out = new ArrayList<>();
        for (CreateMemberRequest m : request) {
            memberValidator.validateMember(m);

            String newId = UUID.randomUUID().toString();
            Member created = new Member();
            created.setId(newId);
            created.setFirstName(m.getFirstName());
            created.setLastName(m.getLastName());
            created.setBirthDate(m.getBirthDate());
            created.setGender(m.getGender());
            created.setAddress(m.getAddress());
            created.setProfession(m.getProfession());
            created.setPhoneNumber(m.getPhoneNumber());
            created.setEmail(m.getEmail());
            created.setOccupation(m.getOccupation());
            created.setReferees(new ArrayList<>());
            
            memberRepository.save(created);

            if (m.getCollectivityIdentifier() != null && !m.getCollectivityIdentifier().isBlank()) {
                memberRepository.updateCollectivityId(newId, m.getCollectivityIdentifier());
            }
            
            if (m.getReferees() != null && !m.getReferees().isEmpty()) {
                memberRepository.saveReferees(newId, m.getReferees());
            }

            created.setReferees(memberRepository.findAllById(m.getReferees()));

            out.add(created);
        }

        return out;
    }
}
