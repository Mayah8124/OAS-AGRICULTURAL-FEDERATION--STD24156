package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.FinancialAccount;

import java.time.LocalDate;
import java.util.List;

public interface CollectivityFinancialAccountRepository {
    boolean collectivityExists(String collectivityId);

    List<FinancialAccount> findFinancialAccountsByCollectivityAt(String collectivityId, LocalDate at);
}
