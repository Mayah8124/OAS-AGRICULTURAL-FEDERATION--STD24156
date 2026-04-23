package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.repository.CollectivityFinancialAccountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class FinancialAccountService {

    private final CollectivityFinancialAccountRepository collectivityFinancialAccountRepository;

    public FinancialAccountService(CollectivityFinancialAccountRepository collectivityFinancialAccountRepository) {
        this.collectivityFinancialAccountRepository = collectivityFinancialAccountRepository;
    }

    public List<FinancialAccount> getCollectivityFinancialAccountsAt(String collectivityId, LocalDate at) {
        if (collectivityId == null || collectivityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Collectivity id is required");
        }
        if (at == null) {
            throw new IllegalArgumentException("Query parameter 'at' is required");
        }

        boolean exists = collectivityFinancialAccountRepository.collectivityExists(collectivityId);
        if (!exists) {
            throw new IllegalArgumentException("Collectivity with id " + collectivityId + " not found");
        }

        return collectivityFinancialAccountRepository.findFinancialAccountsByCollectivityAt(collectivityId, at);
    }
}
