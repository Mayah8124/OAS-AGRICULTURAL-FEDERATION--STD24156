package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.repository.CollectivityRepository;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class GetCollectivityValidator {
    
    private final CollectivityRepository collectivityRepository;
    
    public GetCollectivityValidator(CollectivityRepository collectivityRepository) {
        this.collectivityRepository = collectivityRepository;
    }
    
    public void validateGetCollectivity(String id) {
        validateIdFormat(id);
        validateCollectivityExists(id);
    }
    
    private void validateIdFormat(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidCollectivityException("ID cannot be null or empty");
        }
        
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidCollectivityException("ID must be a valid UUID format");
        }
    }
    
    private void validateCollectivityExists(String id) {
        if (!collectivityRepository.findById(id).isPresent()) {
            throw new CollectivityNotFoundException("Collectivity with ID " + id + " not found");
        }
    }
}
