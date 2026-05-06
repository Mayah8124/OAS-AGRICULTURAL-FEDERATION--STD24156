package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.dto.request.CreateMembershipFeeRequest;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidCollectivityException;
import org.ingredients.agriculturalfederation.validator.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MembershipFeeValidator {

    private final DataSourceConfig dataSourceConfig;

    public MembershipFeeValidator(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void validateCreateRequests(String collectivityId, List<CreateMembershipFeeRequest> requests) {
        if (collectivityId == null || collectivityId.trim().isEmpty()) {
            throw new InvalidCollectivityException("Collectivity identifier is required");
        }

        assertCollectivityExists(collectivityId);

        if (requests == null) {
            throw new InvalidCollectivityException("Request body is required");
        }

        for (CreateMembershipFeeRequest req : requests) {
            if (req == null) {
                throw new InvalidCollectivityException("Membership fee cannot be null");
            }

            if (req.getFrequency() == null) {
                throw new InvalidCollectivityException("Frequency is required");
            }

            if (req.getAmount() == null) {
                throw new InvalidCollectivityException("Amount is required");
            }

            if (req.getAmount().signum() < 0) {
                throw new InvalidCollectivityException("Amount must be greater than or equal to 0");
            }
        }
    }

    private void assertCollectivityExists(String collectivityId) {
        String sql = "SELECT 1 FROM collectivity WHERE id = ? LIMIT 1";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, collectivityId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
            }
            return;
        } catch (SQLException e) {
            throw new ValidationException("Error checking collectivity existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    public void validateCollectivityId(String collectivityId) {
        if (collectivityId == null) {
            throw new IllegalArgumentException("Collectivity ID should not be null");
        }

        if (collectivityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Collectivity ID should not be empty");
        }
    }
}
