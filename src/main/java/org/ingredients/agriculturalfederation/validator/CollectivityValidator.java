package org.ingredients.agriculturalfederation.validator;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.CreateCollectivity;
import org.ingredients.agriculturalfederation.entity.CreateCollectivityStructure;
import org.ingredients.agriculturalfederation.validator.exception.*;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Component
public class CollectivityValidator {

    private final DataSourceConfig dataSourceConfig;

    public CollectivityValidator(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    public void validateCollectivity(CreateCollectivity createCollectivity) {
        if (createCollectivity == null) {
            throw new InvalidCollectivityException("Collectivity cannot be null");
        }

        validateBasicInformation(createCollectivity);
        validateStructure(createCollectivity.getStructure());
        validateMembers(createCollectivity.getMembers());
        validateStructureMembersArePartOfMembers(createCollectivity.getStructure(), createCollectivity.getMembers());
    }

    private void validateBasicInformation(CreateCollectivity createCollectivity) {
        if (createCollectivity.getLocation() == null || createCollectivity.getLocation().trim().isEmpty()) {
            throw new InvalidCollectivityException("Location is required");
        }

        if (createCollectivity.getFederationApproval() == null || !createCollectivity.getFederationApproval()) {
            throw new InvalidCollectivityException("Federation approval is required");
        }

        if (createCollectivity.getStructure() == null) {
            throw new InvalidCollectivityException("Structure is required");
        }
    }

    private void validateStructure(CreateCollectivityStructure structure) {
        if (structure == null) {
            throw new InvalidCollectivityException("Structure cannot be null");
        }

        validateStructureMember(structure.getPresident(), "President");
        validateStructureMember(structure.getVicePresident(), "Vice President");
        validateStructureMember(structure.getTreasurer(), "Treasurer");
        validateStructureMember(structure.getSecretary(), "Secretary");

        validateUniqueStructureMembers(structure);
    }

    private void validateStructureMember(String memberId, String role) {
        if (memberId == null || memberId.trim().isEmpty()) {
            throw new InvalidCollectivityException(role + " is required");
        }

        if (!memberExists(memberId)) {
            throw new MemberNotFoundException(role + " with ID " + memberId + " not found");
        }
    }

    private void validateUniqueStructureMembers(CreateCollectivityStructure structure) {
        String president = structure.getPresident();
        String vicePresident = structure.getVicePresident();
        String treasurer = structure.getTreasurer();
        String secretary = structure.getSecretary();

        if (president.equals(vicePresident)) {
            throw new InvalidCollectivityException("President and Vice President cannot be the same person");
        }

        if (president.equals(treasurer)) {
            throw new InvalidCollectivityException("President and Treasurer cannot be the same person");
        }

        if (president.equals(secretary)) {
            throw new InvalidCollectivityException("President and Secretary cannot be the same person");
        }

        if (vicePresident.equals(treasurer)) {
            throw new InvalidCollectivityException("Vice President and Treasurer cannot be the same person");
        }

        if (vicePresident.equals(secretary)) {
            throw new InvalidCollectivityException("Vice President and Secretary cannot be the same person");
        }

        if (treasurer.equals(secretary)) {
            throw new InvalidCollectivityException("Treasurer and Secretary cannot be the same person");
        }
    }

    private void validateMembers(List<String> members) {
        if (members == null || members.isEmpty()) {
            throw new InvalidCollectivityException("At least one member is required");
        }

        if (members.size() < 10) {
            throw new InvalidCollectivityException("A collectivity must have at least 10 members");
        }

        for (String memberId : members) {
            if (memberId == null || memberId.trim().isEmpty()) {
                throw new InvalidCollectivityException("Member ID cannot be null or empty");
            }

            if (!memberExists(memberId)) {
                throw new MemberNotFoundException("Member with ID " + memberId + " not found");
            }
        }
    }

    private void validateStructureMembersArePartOfMembers(CreateCollectivityStructure structure, List<String> members) {
        if (structure == null || members == null) {
            return;
        }

        if (!members.contains(structure.getPresident())
                || !members.contains(structure.getVicePresident())
                || !members.contains(structure.getTreasurer())
                || !members.contains(structure.getSecretary())) {
            throw new InvalidCollectivityException("Structure members must be part of the collectivity members list");
        }
    }

    private boolean memberExists(String memberId) {
        String sql = "SELECT COUNT(*) FROM member WHERE id = ?";
        Connection connection = null;
        
        try {
            connection = dataSourceConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, UUID.fromString(memberId));
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
}
