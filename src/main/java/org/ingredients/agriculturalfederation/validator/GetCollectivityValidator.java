package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetCollectivityValidator {
    public void validateGetCollectivity(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidCollectivityException("ID cannot be null or empty");
        }
        
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidCollectivityException("ID must be a valid UUID format");
        }
    }
}
