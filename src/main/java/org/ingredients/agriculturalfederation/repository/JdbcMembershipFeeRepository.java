package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbcMembershipFeeRepository implements MembershipFeeRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcMembershipFeeRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public List<MembershipFee> saveAll(String collectivityId, List<MembershipFee> fees) {
        if (fees == null || fees.isEmpty()) {
            return List.of();
        }

        String sql = "INSERT INTO membership_fee (id, collectivity_id, eligible_from, frequency, amount, label, status) VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();

            List<MembershipFee> out = new ArrayList<>();
            for (MembershipFee fee : fees) {
                String id = UUID.randomUUID().toString();
                fee.setId(id);

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setObject(1, UUID.fromString(id));
                    stmt.setObject(2, UUID.fromString(collectivityId));

                    if (fee.getEligibleFrom() == null) {
                        stmt.setNull(3, Types.DATE);
                    } else {
                        stmt.setDate(3, java.sql.Date.valueOf(fee.getEligibleFrom()));
                    }

                    stmt.setString(4, fee.getFrequency() != null ? fee.getFrequency().name() : null);

                    if (fee.getAmount() == null) {
                        stmt.setNull(5, Types.NUMERIC);
                    } else {
                        stmt.setBigDecimal(5, fee.getAmount());
                    }

                    stmt.setString(6, fee.getLabel());
                    stmt.setString(7, fee.getStatus() != null ? fee.getStatus().name() : null);

                    stmt.executeUpdate();
                }

                out.add(fee);
            }

            return out;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership fees", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }
}
