package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.CreateMember;
import org.ingredients.agriculturalfederation.validator.exception.*;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MemberValidator {

    private final DataSourceConfig dataSourceConfig;

    public MemberValidator(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void validateMember(CreateMember createMember) {
        if (createMember == null) {
            throw new InvalidMemberException("Member cannot be null");
        }

        validateBasicInformation(createMember);
        validatePaymentStatus(createMember);
        validateReferees(createMember);
        validateCollectivity(createMember.getCollectivityIdentifier());
    }

    private void validateBasicInformation(CreateMember createMember) {
        if (createMember.getFirstName() == null || createMember.getFirstName().trim().isEmpty()) {
            throw new InvalidMemberException("First name is required");
        }

        if (createMember.getLastName() == null || createMember.getLastName().trim().isEmpty()) {
            throw new InvalidMemberException("Last name is required");
        }

        if (createMember.getBirthDate() == null) {
            throw new InvalidMemberException("Birth date is required");
        }

        if (createMember.getGender() == null) {
            throw new InvalidMemberException("Gender is required");
        }

        if (createMember.getAddress() == null || createMember.getAddress().trim().isEmpty()) {
            throw new InvalidMemberException("Address is required");
        }

        if (createMember.getProfession() == null || createMember.getProfession().trim().isEmpty()) {
            throw new InvalidMemberException("Profession is required");
        }

        if (createMember.getPhoneNumber() == null) {
            throw new InvalidMemberException("Phone number is required");
        }

        if (createMember.getEmail() == null || createMember.getEmail().trim().isEmpty()) {
            throw new InvalidMemberException("Email is required");
        }

        if (!isValidEmail(createMember.getEmail())) {
            throw new InvalidMemberException("Invalid email format");
        }
    }

    private void validatePaymentStatus(CreateMember createMember) {
        if (createMember.getRegistrationFeePaid() == null || !createMember.getRegistrationFeePaid()) {
            throw new InvalidMemberException("Registration fee must be paid");
        }

        if (createMember.getMembershipDuesPaid() == null || !createMember.getMembershipDuesPaid()) {
            throw new InvalidMemberException("Membership dues must be paid");
        }
    }

    private void validateReferees(CreateMember createMember) {
        List<String> referees = createMember.getReferees();
        if (referees == null || referees.isEmpty()) {
            throw new InvalidMemberException("At least one referee is required");
        }

        for (String refereeId : referees) {
            if (!memberExists(refereeId)) {
                throw new InvalidMemberException("Referee with ID " + refereeId + " not found");
            }
        }
    }

    private void validateCollectivity(String collectivityIdentifier) {
        if (collectivityIdentifier == null || collectivityIdentifier.trim().isEmpty()) {
            throw new InvalidMemberException("Collectivity identifier is required");
        }

        if (!collectivityExists(collectivityIdentifier)) {
            throw new CollectivityNotFoundException("Collectivity with ID " + collectivityIdentifier + " not found");
        }
    }

    private boolean memberExists(String memberId) {
        String sql = "SELECT COUNT(*) FROM members WHERE id = ?";
        Connection connection = null;
        
        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, memberId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new ValidationException("Error checking member existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
        
        return false;
    }

    private boolean collectivityExists(String collectivityId) {
        String sql = "SELECT COUNT(*) FROM collectivities WHERE id = ?";
        Connection connection = null;
        
        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, collectivityId);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            throw new ValidationException("Error checking collectivity existence", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
        
        return false;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }
}
