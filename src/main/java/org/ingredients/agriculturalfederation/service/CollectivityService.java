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
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CollectivityService {

    private final CollectivityValidator collectivityValidator;
    private final CollectivityRepository collectivityRepository;
    private final MemberRepository memberRepository;

    public CollectivityService(CollectivityValidator collectivityValidator, CollectivityRepository collectivityRepository, MemberRepository memberRepository) {
        this.collectivityValidator = collectivityValidator;
        this.collectivityRepository = collectivityRepository;
        this.memberRepository = memberRepository;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivityRequest> requests) {
        if (requests == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivityRequest req : requests) {
            collectivityValidator.validateCollectivity(req);

            Collectivity collectivity = new Collectivity();
            collectivity.setId(UUID.randomUUID().toString());
            collectivity.setLocation(req.getLocation());
            collectivity.setName(req.getName());
            collectivity.setNumber(req.getNumber());
            collectivity.setStructure(toEntityStructure(req.getStructure()));
            collectivity.setMembers(new ArrayList<>());

            collectivityRepository.save(collectivity, false);
            out.add(collectivity);
        }
        return out;
    }

    public Collectivity updateInformations(String id, CollectivityInformationRequest request) {
        collectivityRepository.assignIdentity(id, request.getName(), request.getNumber());
        return collectivityRepository.findById(id).orElseThrow();
    }

    private CollectivityStructure toEntityStructure(CreateCollectivityStructureRequest structure) {
        if (structure == null) {
            return null;
        }

        Member president = memberRepository.findById(structure.getPresident()).orElse(null);
        Member vicePresident = memberRepository.findById(structure.getVicePresident()).orElse(null);
        Member treasurer = memberRepository.findById(structure.getTreasurer()).orElse(null);
        Member secretary = memberRepository.findById(structure.getSecretary()).orElse(null);

        return new CollectivityStructure(president, vicePresident, treasurer, secretary);
    }

    public Collectivity getCollectivityById(String id) {
        return collectivityRepository.findById(id)
                .orElseThrow(() -> new CollectivityNotFoundException("Collectivity with ID " + id + " not found"));
    }
}
