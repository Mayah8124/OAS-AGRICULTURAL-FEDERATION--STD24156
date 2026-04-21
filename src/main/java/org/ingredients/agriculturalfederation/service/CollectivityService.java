package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.entity.Member;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class CollectivityService {

    public List<Collectivity> createCollectivities(List<CreateCollectivity> request) {
        if (request == null) {
            return List.of();
        }

        List<Collectivity> out = new ArrayList<>();
        for (CreateCollectivity c : request) {
            validateCollectivityCreation(c);
            for (String memberId : c.getMembers()) {
                if (memberId == null || memberId.isBlank()) {
                    throw new RuntimeException("Member not found.");
                }
            }

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

    private static void validateCollectivityCreation(CreateCollectivity c) {
        if (c == null) {
            throw new RuntimeException("Request body is required");
        }
        if (c.getFederationApproval() == null || !c.getFederationApproval() || c.getStructure() == null) {
            throw new RuntimeException("Collectivity without federation approval or structure missing.");
        }
        if (c.getMembers() == null || c.getMembers().isEmpty()) {
            throw new RuntimeException("Members list is required");
        }
        if (c.getStructure().getPresident() == null || c.getStructure().getVicePresident() == null ||
                c.getStructure().getTreasurer() == null || c.getStructure().getSecretary() == null) {
            throw new RuntimeException("Collectivity without federation approval or structure missing.");
        }
        HashSet<String> ids = new HashSet<>(c.getMembers());
        if (ids.size() != c.getMembers().size()) {
            throw new RuntimeException("Duplicate member identifiers in members list");
        }
    }
}
