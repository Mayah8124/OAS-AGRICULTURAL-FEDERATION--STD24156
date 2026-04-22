package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.CollectivityTransaction;
import org.ingredients.agriculturalfederation.entity.CashAccount;
import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.entity.PaymentMode;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcCollectivityTransactionRepository implements CollectivityTransactionRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcCollectivityTransactionRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public List<CollectivityTransaction> findByCollectivityIdAndDateRange(String collectivityId, LocalDate fromDate, LocalDate toDate) {
        String sql = "SELECT id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date " +
                "FROM collectivity_transaction " +
                "WHERE collectivity_id = ? AND creation_date >= ? AND creation_date <= ? " +
                "ORDER BY creation_date DESC";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, java.util.UUID.fromString(collectivityId));
                statement.setDate(2, Date.valueOf(fromDate));
                statement.setDate(3, Date.valueOf(toDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<CollectivityTransaction> transactions = new ArrayList<>();
                    while (resultSet.next()) {
                        transactions.add(mapRow(resultSet));
                    }
                    return transactions;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public boolean existsById(String collectivityId) {
        String sql = "SELECT 1 FROM collectivity WHERE id = ? LIMIT 1";
        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, java.util.UUID.fromString(collectivityId));
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public CollectivityTransaction save(CollectivityTransaction transaction) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public CollectivityTransaction findById(String transactionId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public List<CollectivityTransaction> findByCollectivityId(String collectivityId) {
        throw new UnsupportedOperationException("Not implemented");
    }

    private static CollectivityTransaction mapRow(ResultSet resultSet) throws SQLException {
        CollectivityTransaction transaction = new CollectivityTransaction();
        transaction.setId(resultSet.getObject("id").toString());
        transaction.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
        transaction.setAmount(resultSet.getBigDecimal("amount"));

        String paymentMode = resultSet.getString("payment_mode");
        transaction.setPaymentMode(paymentMode == null ? null : PaymentMode.valueOf(paymentMode));

        FinancialAccount accountCredited = new CashAccount();
        String accountCreditedId = resultSet.getString("account_credited_id");
        if (accountCreditedId != null) {
            accountCredited.setId(accountCreditedId);
        }
        transaction.setAccountCredited(accountCreditedId == null ? null : accountCredited);

        Member memberDebited = new Member();
        String memberDebitedId = resultSet.getString("member_debited_id");
        if (memberDebitedId != null) {
            memberDebited.setId(memberDebitedId);
        }
        transaction.setMemberDebited(memberDebitedId == null ? null : memberDebited);

        return transaction;
    }
}
