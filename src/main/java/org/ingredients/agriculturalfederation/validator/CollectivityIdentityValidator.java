package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.AssignCollectivityIdentity;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.ingredients.agriculturalfederation.validator.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
public class CollectivityIdentityValidator {

    private final DataSourceConfig dataSourceConfig;

    public CollectivityIdentityValidator(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void validate(String collectivityId, AssignCollectivityIdentity request) {
        if (collectivityId == null || collectivityId.trim().isEmpty()) {
            throw new InvalidCollectivityException("Collectivity identifier is required");
        }

        if (request == null) {
            throw new InvalidCollectivityException("Identity payload cannot be null");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new InvalidCollectivityException("Collectivity name is required");
        }

        String trimmedName = request.getName().trim();
        if (trimmedName.length() < 3) {
            throw new InvalidCollectivityException("Collectivity name must be at least 3 characters long");
        }

        if (trimmedName.length() > 100) {
            throw new InvalidCollectivityException("Collectivity name must be at most 100 characters long");
        }

        if (request.getNumber() == null) {
            throw new InvalidCollectivityException("Collectivity number is required");
        }

        if (request.getNumber() < 1) {
            throw new InvalidCollectivityException("Collectivity number must be greater than or equal to 1");
        }

        assertCollectivityExists(collectivityId);
    }

    private void assertCollectivityExists(String collectivityId) {
        String sql = "SELECT COUNT(*) FROM collectivity WHERE id = ?";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, UUID.fromString(collectivityId));
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                if (resultSet.getInt(1) == 0) {
                    throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
                }
                return;
            }

            throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
        } catch (SQLException e) {
            throw new ValidationException("Error checking collectivity existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }
}
