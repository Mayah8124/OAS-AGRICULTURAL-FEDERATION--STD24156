package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.InvalidMemberException;
import org.ingredients.agriculturalfederation.validator.exception.MemberNotFoundException;
import org.ingredients.agriculturalfederation.validator.exception.ValidationException;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MemberPaymentValidator {

    private final DataSourceConfig dataSourceConfig;

    public MemberPaymentValidator(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void validateCreateRequests(String memberId, List<CreateMemberPaymentRequest> requests) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new InvalidMemberException("Member identifier is required");
        }

        if (requests == null) {
            throw new InvalidMemberException("Request body is required");
        }

        assertMemberExists(memberId);
        assertMemberHasCollectivity(memberId);

        for (CreateMemberPaymentRequest req : requests) {
            if (req == null) {
                throw new InvalidMemberException("Payment cannot be null");
            }

            if (req.getAmount() == null) {
                throw new InvalidMemberException("Amount is required");
            }

            if (req.getAmount().signum() < 0) {
                throw new InvalidMemberException("Amount must be greater than or equal to 0");
            }

            if (req.getAccountCreditedIdentifier() == null || req.getAccountCreditedIdentifier().trim().isEmpty()) {
                throw new InvalidMemberException("Account credited identifier is required");
            }

            if (req.getPaymentMode() == null) {
                throw new InvalidMemberException("Payment mode is required");
            }

            assertFinancialAccountExists(req.getAccountCreditedIdentifier());

            if (req.getMembershipFeeIdentifier() != null && !req.getMembershipFeeIdentifier().trim().isEmpty()) {
                assertMembershipFeeExists(req.getMembershipFeeIdentifier());
            }
        }
    }

    public String getMemberCollectivityId(String memberId) {
        String sql = "SELECT collectivity_id FROM member WHERE id = ?";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new MemberNotFoundException("Member with ID " + memberId + " not found");
            }

            Object collectivityId = resultSet.getObject("collectivity_id");
            if (collectivityId == null) {
                throw new CollectivityNotFoundException("Collectivity not found");
            }

            return collectivityId.toString();
        } catch (SQLException e) {
            throw new ValidationException("Error checking member collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    private void assertMemberExists(String memberId) {
        String sql = "SELECT 1 FROM member WHERE id = ? LIMIT 1";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new MemberNotFoundException("Member with ID " + memberId + " not found");
            }
        } catch (SQLException e) {
            throw new ValidationException("Error checking member existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    private void assertMemberHasCollectivity(String memberId) {
        String sql = "SELECT collectivity_id FROM member WHERE id = ?";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new MemberNotFoundException("Member with ID " + memberId + " not found");
            }

            if (resultSet.getObject("collectivity_id") == null) {
                throw new CollectivityNotFoundException("Collectivity not found");
            }
        } catch (SQLException e) {
            throw new ValidationException("Error checking member collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    private void assertFinancialAccountExists(String financialAccountId) {
        String sql = "SELECT 1 FROM financial_account WHERE id = ? LIMIT 1";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, financialAccountId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new InvalidMemberException("Financial account with ID " + financialAccountId + " not found");
            }
        } catch (SQLException e) {
            throw new ValidationException("Error checking financial account existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    private void assertMembershipFeeExists(String membershipFeeId) {
        String sql = "SELECT 1 FROM membership_fee WHERE id = ? LIMIT 1";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, membershipFeeId);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                throw new InvalidMemberException("Membership fee with ID " + membershipFeeId + " not found");
            }
        } catch (SQLException e) {
            throw new ValidationException("Error checking membership fee existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }
}
