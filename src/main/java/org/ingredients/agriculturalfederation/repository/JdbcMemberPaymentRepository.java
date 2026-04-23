package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.dto.request.CreateMemberPaymentRequest;
import org.ingredients.agriculturalfederation.dto.response.AccountCreditedResponse;
import org.ingredients.agriculturalfederation.dto.response.MemberDebitedResponse;
import org.ingredients.agriculturalfederation.dto.response.MemberPaymentResponse;
import org.ingredients.agriculturalfederation.entity.PaymentMode;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                String paymentId = UUID.randomUUID().toString();
                String transactionId = UUID.randomUUID().toString();

                try (PreparedStatement stmtPayment = conn.prepareStatement(sqlPayment);
                     PreparedStatement stmtTransaction = conn.prepareStatement(sqlTransaction);
                     PreparedStatement stmtLinkAccount = conn.prepareStatement(sqlLinkAccount);
                     PreparedStatement stmtUpsertBalance = conn.prepareStatement(sqlUpsertBalance)) {

                    stmtPayment.setObject(1, UUID.fromString(paymentId));
                    stmtPayment.setObject(2, UUID.fromString(memberId));

                    if (req.getMembershipFeeIdentifier() == null || req.getMembershipFeeIdentifier().trim().isEmpty()) {
                        stmtPayment.setNull(3, Types.OTHER);
                    } else {
                        stmtPayment.setObject(3, UUID.fromString(req.getMembershipFeeIdentifier()));
                    }

                    stmtPayment.setObject(4, UUID.fromString(req.getAccountCreditedIdentifier()));
                    stmtPayment.setBigDecimal(5, req.getAmount());
                    stmtPayment.setString(6, req.getPaymentMode().name());
                    stmtPayment.setDate(7, Date.valueOf(today));
                    stmtPayment.executeUpdate();

                    stmtTransaction.setObject(1, UUID.fromString(transactionId));
                    stmtTransaction.setObject(2, UUID.fromString(collectivityId));
                    stmtTransaction.setObject(3, UUID.fromString(memberId));
                    stmtTransaction.setObject(4, UUID.fromString(req.getAccountCreditedIdentifier()));
                    stmtTransaction.setBigDecimal(5, req.getAmount());
                    stmtTransaction.setString(6, req.getPaymentMode().name());
                    stmtTransaction.setDate(7, Date.valueOf(today));
                    stmtTransaction.executeUpdate();

                    stmtLinkAccount.setObject(1, UUID.fromString(collectivityId));
                    stmtLinkAccount.setObject(2, UUID.fromString(req.getAccountCreditedIdentifier()));
                    stmtLinkAccount.executeUpdate();

                    stmtUpsertBalance.setObject(1, UUID.fromString(req.getAccountCreditedIdentifier()));
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
}
