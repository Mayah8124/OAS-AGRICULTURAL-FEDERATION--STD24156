package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionsValidator {

    public void validateParameters(String collectivityId, LocalDate from, LocalDate to) {
        if (collectivityId == null || from == null || to == null) {
            throw new IllegalArgumentException("CollectivityId and the dates are required");
        }

        if (collectivityId.isEmpty()) {
            throw new IllegalArgumentException("CollectivityId cannot be empty");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
    }

    public void validateCollectivityExists(String collectivityId, boolean exists) {
        if (!exists) {
            throw new CollectivityNotFoundException("Collectivity with id " + collectivityId + " not found");
        }
    }
}
