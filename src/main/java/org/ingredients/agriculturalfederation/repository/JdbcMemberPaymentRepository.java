package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.dto.response.AccountCreditedResponse;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.entity.CollectivityPaymentStats;
import org.ingredients.agriculturalfederation.entity.PaymentMode;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcMemberPaymentRepository implements MemberPaymentRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcMemberPaymentRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public List<MemberPaymentResponse> createPayments(String memberId, String collectivityId, List<CreateMemberPaymentRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        String sqlPayment = "INSERT INTO member_payment (id, member_id, membership_fee_id, account_credited_id, amount, payment_mode, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlTransaction = "INSERT INTO collectivity_transaction (id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlLinkAccount = "INSERT INTO collectivity_financial_account (collectivity_id, financial_account_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        String sqlUpsertBalance = "INSERT INTO financial_account_balance (financial_account_id, at_date, amount) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (financial_account_id, at_date) DO UPDATE SET amount = financial_account_balance.amount + EXCLUDED.amount";

        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            conn.setAutoCommit(false);

            List<MemberPaymentResponse> out = new ArrayList<>();
            LocalDate today = LocalDate.now();

            for (CreateMemberPaymentRequest req : requests) {
                String paymentId = java.util.UUID.randomUUID().toString();
                String transactionId = java.util.UUID.randomUUID().toString();

                try (PreparedStatement stmtPayment = conn.prepareStatement(sqlPayment);
                     PreparedStatement stmtTransaction = conn.prepareStatement(sqlTransaction);
                     PreparedStatement stmtLinkAccount = conn.prepareStatement(sqlLinkAccount);
                     PreparedStatement stmtUpsertBalance = conn.prepareStatement(sqlUpsertBalance)) {

                    stmtPayment.setString(1, paymentId);
                    stmtPayment.setString(2, memberId);

                    if (req.getMembershipFeeIdentifier() == null || req.getMembershipFeeIdentifier().trim().isEmpty()) {
                        stmtPayment.setNull(3, Types.VARCHAR);
                    } else {
                        stmtPayment.setString(3, req.getMembershipFeeIdentifier());
                    }

                    stmtPayment.setString(4, req.getAccountCreditedIdentifier());
                    stmtPayment.setBigDecimal(5, req.getAmount());
                    stmtPayment.setString(6, req.getPaymentMode().name());
                    stmtPayment.setDate(7, Date.valueOf(today));
                    stmtPayment.executeUpdate();

                    stmtTransaction.setString(1, transactionId);
                    stmtTransaction.setString(2, collectivityId);
                    stmtTransaction.setString(3, memberId);
                    stmtTransaction.setString(4, req.getAccountCreditedIdentifier());
                    stmtTransaction.setBigDecimal(5, req.getAmount());
                    stmtTransaction.setString(6, req.getPaymentMode().name());
                    stmtTransaction.setDate(7, Date.valueOf(today));
                    stmtTransaction.executeUpdate();

                    stmtLinkAccount.setString(1, collectivityId);
                    stmtLinkAccount.setString(2, req.getAccountCreditedIdentifier());
                    stmtLinkAccount.executeUpdate();

                    stmtUpsertBalance.setString(1, req.getAccountCreditedIdentifier());
                    stmtUpsertBalance.setDate(2, Date.valueOf(today));
                    stmtUpsertBalance.setBigDecimal(3, req.getAmount());
                    stmtUpsertBalance.executeUpdate();
                }

                out.add(MemberPaymentResponse.builder()
                        .id(paymentId)
                        .amount(req.getAmount() == null ? BigDecimal.ZERO : req.getAmount())
                        .paymentMode(req.getPaymentMode() == null ? PaymentMode.CASH.name() : req.getPaymentMode().name())
                        .accountCredited(AccountCreditedResponse.builder().id(req.getAccountCreditedIdentifier()).build())
                        .creationDate(today)
                        .build());
            }

            conn.commit();
            return out;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Error creating member payments", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                }
                dataSourceConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public List<MemberPaymentResponse> findByMemberIdAndDateRange(String memberId, LocalDate from, LocalDate to) {
        if (memberId == null || memberId.trim().isEmpty() || from == null || to == null) {
            return List.of();
        }

        String sql = """
                        SELECT id, amount, payment_mode, account_credited_id, creation_date 
                        FROM member_payment 
                        WHERE member_id = ? AND creation_date BETWEEN ? AND ?
                        ORDER BY creation_date
                """;

        List<MemberPaymentResponse> payments = new ArrayList<>();

        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    MemberPaymentResponse payment = new MemberPaymentResponse();
                    payment.setId(rs.getString("id"));
                    payment.setAmount(rs.getBigDecimal("amount"));
                    payment.setPaymentMode(rs.getString("payment_mode"));
                    payment.setCreationDate(rs.getDate("creation_date").toLocalDate());

                    AccountCreditedResponse accountCredited = new AccountCreditedResponse();
                    accountCredited.setId(rs.getString("account_credited_id"));
                    payment.setAccountCredited(accountCredited);

                    payments.add(payment);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member payments in date range", e);
        }

        return payments;
    }

    @Override
    public double findTotalEarnedByMemberIdAndDateRange(String memberId, LocalDate from, LocalDate to) {
        if (memberId == null || memberId.trim().isEmpty() || from == null || to == null) {
            return 0.0;
        }

        String sql = "SELECT SUM(amount) FROM member_payment WHERE member_id = ? AND creation_date BETWEEN ? AND ?";

        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal sum = rs.getBigDecimal(1);
                    return sum != null ? sum.doubleValue() : 0.0;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error calculating total earned for member", e);
        }
        return 0.0;
    }

    @Override
    public List<CollectivityPaymentStats> getCollectivityPaymentStats(LocalDate from, LocalDate to) {
        String sql = """
            SELECT 
                c.id as collectivity_id,
                COUNT(DISTINCT m.id) as total_members,
                COUNT(DISTINCT CASE WHEN mp_first.first_payment_date >= ? THEN m.id END) as members_current_with_dues,
                -- BUG-11 fix: count members who joined during the period (creation_date in range), not a duplicate of above
                COUNT(DISTINCT CASE WHEN m.creation_date BETWEEN ? AND ? THEN m.id END) as new_members_count
            FROM collectivity c
            LEFT JOIN member m ON m.collectivity_id = c.id
            LEFT JOIN (
                SELECT 
                    mp.member_id,
                    MIN(mp.creation_date) as first_payment_date
                FROM member_payment mp
                WHERE mp.creation_date BETWEEN ? AND ?
                GROUP BY mp.member_id
            ) mp_first ON mp_first.member_id = m.id
            GROUP BY c.id
            ORDER BY c.id
            """;

        List<CollectivityPaymentStats> stats = new ArrayList<>();
        
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(from));
            stmt.setDate(3, java.sql.Date.valueOf(to));
            stmt.setDate(4, java.sql.Date.valueOf(from));
            stmt.setDate(5, java.sql.Date.valueOf(to));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String collectivityId = rs.getString("collectivity_id");
                    long totalMembers = rs.getLong("total_members");
                    long membersCurrentWithDues = rs.getLong("members_current_with_dues");
                    long newMembersCount = rs.getLong("new_members_count");
                    
                    stats.add(new CollectivityPaymentStats(collectivityId, totalMembers, membersCurrentWithDues, newMembersCount));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving collectivity payment statistics", e);
        }
        
        return stats;
    }
}
