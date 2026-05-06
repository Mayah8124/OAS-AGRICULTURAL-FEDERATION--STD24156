package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.ActivityStatus;
import org.ingredients.agriculturalfederation.entity.Frequency;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                String id = java.util.UUID.randomUUID().toString();
                fee.setId(id);

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, id);
                    stmt.setString(2, collectivityId);

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

    @Override
    public List<MembershipFee> findByCollectivityId(String collectivityId) {
        String sql = """
                        SELECT id, eligible_from, frequency, amount, label, status 
                        FROM membership_fee WHERE collectivity_id = ?
                """;

        List<MembershipFee> membershipFees = new ArrayList<>();
        Connection conn = null;

        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MembershipFee membershipFee = new MembershipFee();
                membershipFee.setId(rs.getObject("id").toString());
                membershipFee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                membershipFee.setFrequency(Frequency.valueOf(rs.getString("frequency")));
                membershipFee.setAmount(rs.getBigDecimal("amount"));
                membershipFee.setLabel(rs.getString("label"));
                membershipFee.setStatus(ActivityStatus.valueOf(rs.getString("status")));

                membershipFees.add(membershipFee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding membership fees for collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }

        return membershipFees;
    }

    @Override
    public void save(MembershipFee membershipFee) {
        String sql = """
                        INSERT INTO membership_fee (id, eligible_from, frequency, amount, label, status, collectivity_id) 
                        VALUES (?, ?, ?, ?, ?, ?, ?)
                     """;

        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, membershipFee.getId());
            stmt.setDate(2, Date.valueOf(membershipFee.getEligibleFrom()));
            stmt.setString(3, membershipFee.getFrequency().name());
            stmt.setBigDecimal(4, membershipFee.getAmount());
            stmt.setString(5, membershipFee.getLabel());
            stmt.setString(6, membershipFee.getStatus().name());
            stmt.setString(7, membershipFee.getCollectivityId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership fee", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }
}
