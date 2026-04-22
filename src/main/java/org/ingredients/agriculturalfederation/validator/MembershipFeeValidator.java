package org.ingredients.agriculturalfederation.validator;

import org.springframework.stereotype.Component;

@Component
public class MembershipFeeValidator {

    public void validateCollectivityId(String collectivityId) {
        if (collectivityId == null) {
            throw new IllegalArgumentException("Collectivity ID should not be null");
        }
        
        if (collectivityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Collectivity ID should not be empty");
        }
        
        if (!isValidUUID(collectivityId)) {
            throw new IllegalArgumentException("The collectivity ID must be a valid UUID");
        }
    }

    private boolean isValidUUID(String uuid) {
        try {
            java.util.UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
