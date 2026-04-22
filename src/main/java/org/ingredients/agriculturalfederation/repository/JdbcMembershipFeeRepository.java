package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.ActivityStatus;
import org.ingredients.agriculturalfederation.entity.Frequency;
import org.ingredients.agriculturalfederation.entity.MembershipFee;
import org.springframework.stereotype.Repository;

import java.sql.*;
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
            stmt.setObject(1, UUID.fromString(collectivityId));
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
            
            stmt.setObject(1, UUID.fromString(membershipFee.getId()));
            stmt.setDate(2, Date.valueOf(membershipFee.getEligibleFrom()));
            stmt.setString(3, membershipFee.getFrequency().name());
            stmt.setBigDecimal(4, membershipFee.getAmount());
            stmt.setString(5, membershipFee.getLabel());
            stmt.setString(6, membershipFee.getStatus().name());
            stmt.setObject(7, UUID.fromString(membershipFee.getCollectivityId()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving membership fee", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }
}
