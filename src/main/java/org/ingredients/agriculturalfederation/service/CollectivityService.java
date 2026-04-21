package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.validator.CollectivityValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CollectivityService {

    private final CollectivityValidator collectivityValidator;

    public CollectivityService(CollectivityValidator collectivityValidator) {
        this.collectivityValidator = collectivityValidator;
    }

    public List<Collectivity> createCollectivities(List<CreateCollectivity> request) {
        if (request == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivity c : request) {
            collectivityValidator.validateCollectivity(c);

            CollectivityStructure structure = new CollectivityStructure(
                    new Member(c.getStructure().getPresident(), null),
                    new Member(c.getStructure().getVicePresident(), null),
                    new Member(c.getStructure().getTreasurer(), null),
                    new Member(c.getStructure().getSecretary(), null)
            );

            List<Member> members = new ArrayList<>();
            for (String memberId : c.getMembers()) {
                members.add(new Member(memberId, null));
            }

            out.add(new Collectivity("generated", c.getLocation(), structure, members));
        }

        return out;
    }
}
