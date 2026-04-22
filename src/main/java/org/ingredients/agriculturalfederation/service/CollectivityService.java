package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.dto.request.CollectivityInformationRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityRequest;
import org.ingredients.agriculturalfederation.dto.request.CreateCollectivityStructureRequest;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.repository.CollectivityRepository;
import org.ingredients.agriculturalfederation.repository.MemberRepository;
import org.ingredients.agriculturalfederation.validator.CollectivityValidator;
import org.ingredients.agriculturalfederation.validator.CollectivityIdentityValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    private final CollectivityValidator collectivityValidator;
    private final CollectivityIdentityValidator collectivityIdentityValidator;
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityValidator collectivityValidator, 
                               CollectivityIdentityValidator collectivityIdentityValidator,
                               CollectivityRepository collectivityRepository,
                               MemberRepository memberRepository) {
        this.collectivityValidator = collectivityValidator;
        this.collectivityIdentityValidator = collectivityIdentityValidator;
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivityRequest> request) {
        if (request == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivityRequest c : request) {
            collectivityValidator.validateCollectivity(c);

            String collectivityId = UUID.randomUUID().toString();
            
            CreateCollectivityStructureRequest s = c.getStructure();
            Member president = memberRepository.findById(s.getPresident()).orElseThrow();
            Member vicePresident = memberRepository.findById(s.getVicePresident()).orElseThrow();
            Member treasurer = memberRepository.findById(s.getTreasurer()).orElseThrow();
            Member secretary = memberRepository.findById(s.getSecretary()).orElseThrow();

            CollectivityStructure structure = new CollectivityStructure(
                    president,
                    vicePresident,
                    treasurer,
                    secretary
            );

            List<Member> members = memberRepository.findAllById(c.getMembers());

            Collectivity collectivity = new Collectivity(collectivityId, c.getLocation(), null, null, structure, members);
            
            collectivityRepository.save(collectivity, c.getFederationApproval());
            collectivityRepository.linkMembers(collectivityId, c.getMembers());

            out.add(collectivity);
        }

        return out;
    }

    public Collectivity updateInformations(String collectivityId, CollectivityInformationRequest request) {
        collectivityIdentityValidator.validate(collectivityId, request);
        collectivityRepository.assignIdentity(collectivityId, request.getName(), request.getNumber());
        return collectivityRepository.findById(collectivityId).orElseThrow();
    }
}
