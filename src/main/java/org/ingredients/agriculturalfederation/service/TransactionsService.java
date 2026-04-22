package org.ingredients.agriculturalfederation.service;

import org.ingredients.agriculturalfederation.entity.CollectivityTransaction;
import org.ingredients.agriculturalfederation.repository.CollectivityTransactionRepository;
import org.ingredients.agriculturalfederation.validator.TransactionsValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionsService {
    private final CollectivityTransactionRepository collectivityTransactionRepository;
    private final TransactionsValidator transactionsValidator;

    public TransactionsService(CollectivityTransactionRepository collectivityTransactionRepository, TransactionsValidator transactionsValidator) {
        this.collectivityTransactionRepository = collectivityTransactionRepository;
        this.transactionsValidator = transactionsValidator;
    }

    public List<CollectivityTransaction> getCollectivityTransactionsBetween(
            String collectivityId,
            LocalDate from,
            LocalDate to
    ) {
        transactionsValidator.validateParameters(collectivityId, from, to);
        boolean collectivityExists = collectivityTransactionRepository.existsById(collectivityId);
        transactionsValidator.validateCollectivityExists(collectivityId, collectivityExists);
        return collectivityTransactionRepository.findByCollectivityIdAndDateRange(collectivityId, from, to);
    }
}
