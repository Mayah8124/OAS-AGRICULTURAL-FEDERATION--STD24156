package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.repository.CollectivityRepository;
import org.ingredients.agriculturalfederation.repository.MemberRepository;
import org.ingredients.agriculturalfederation.validator.CollectivityValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    private final CollectivityValidator collectivityValidator;
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityValidator collectivityValidator, 
                               CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository) {
        this.collectivityValidator = collectivityValidator;
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivity> request) {
        if (request == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivity c : request) {
            collectivityValidator.validateCollectivity(c);

            String collectivityId = UUID.randomUUID().toString();
            
            Member president = memberRepository.findById(c.getStructure().getPresident()).orElseThrow();
            Member vicePresident = memberRepository.findById(c.getStructure().getVicePresident()).orElseThrow();
            Member treasurer = memberRepository.findById(c.getStructure().getTreasurer()).orElseThrow();
            Member secretary = memberRepository.findById(c.getStructure().getSecretary()).orElseThrow();

            CollectivityStructure structure = new CollectivityStructure(
                    president,
                    vicePresident,
                    treasurer,
                    secretary
            );

            List<Member> members = memberRepository.findAllById(c.getMembers());

            Collectivity collectivity = new Collectivity(collectivityId, c.getLocation(), structure, members);
            
            collectivityRepository.save(collectivity, c.getFederationApproval());
            collectivityRepository.linkMembers(collectivityId, c.getMembers());

            out.add(collectivity);
        }

        return out;
    }
}
