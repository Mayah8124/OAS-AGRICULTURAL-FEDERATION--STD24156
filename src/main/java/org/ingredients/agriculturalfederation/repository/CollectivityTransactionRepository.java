package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.entity.CollectivityTransaction;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CollectivityTransactionRepository {
    List<CollectivityTransaction> findByCollectivityIdAndDateRange(String collectivityId, LocalDate fromDate, LocalDate toDate);
    boolean existsById(String collectivityId);
    CollectivityTransaction save(CollectivityTransaction transaction);
    CollectivityTransaction findById(String transactionId);
    List<CollectivityTransaction> findByCollectivityId(String collectivityId);
}
