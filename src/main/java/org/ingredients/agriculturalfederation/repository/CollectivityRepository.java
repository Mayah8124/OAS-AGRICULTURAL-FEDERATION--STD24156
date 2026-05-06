package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.Collectivity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CollectivityRepository {
    void save(Collectivity collectivity, boolean federationApproval);
    Optional<Collectivity> findById(String id);
    Optional<Collectivity> findByIdWithMembers(String id);
    void linkMembers(String collectivityId, java.util.List<String> memberIds);
    void assignIdentity(String collectivityId, String name, Integer number);
    List<Collectivity> findAll();
    long countNewMembers(String collectivityId, LocalDate from, LocalDate to);
}
