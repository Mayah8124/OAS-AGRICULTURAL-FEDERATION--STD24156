package org.ingredients.agriculturalfederation.validator;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionsValidator {
    public void validateParameters(String CollectivityId, LocalDate from , LocalDate to){
        if (CollectivityId == null || from == null || to == null){
            throw new IllegalArgumentException("CollectivityId and the dates are required");
        }

        if (CollectivityId.isEmpty()){
            throw new IllegalArgumentException("CollectivityId cannot be empty");
        }
        
        if (from.isAfter(to)){
            throw new IllegalArgumentException("From date cannot be after to date");
        }

    }
}
