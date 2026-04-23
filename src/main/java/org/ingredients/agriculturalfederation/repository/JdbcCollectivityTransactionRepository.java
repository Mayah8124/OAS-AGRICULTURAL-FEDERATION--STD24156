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
        String sql = "INSERT INTO collectivity_transaction (id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        String sqlCollectivityIdByMember = "SELECT collectivity_id FROM member WHERE id = ?";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                if (transaction == null) {
                    throw new IllegalArgumentException("Transaction is required");
                }
                if (transaction.getId() == null || transaction.getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Transaction id is required");
                }
                if (transaction.getMemberDebited() == null || transaction.getMemberDebited().getId() == null || transaction.getMemberDebited().getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Transaction memberDebited.id is required to derive collectivity id");
                }
                if (transaction.getAccountCredited() == null || transaction.getAccountCredited().getId() == null || transaction.getAccountCredited().getId().trim().isEmpty()) {
                    throw new IllegalArgumentException("Transaction accountCredited.id is required");
                }
                if (transaction.getCreationDate() == null) {
                    throw new IllegalArgumentException("Transaction creationDate is required");
                }
                if (transaction.getPaymentMode() == null) {
                    throw new IllegalArgumentException("Transaction paymentMode is required");
                }

                String collectivityId;
                try (PreparedStatement stmtCollectivity = connection.prepareStatement(sqlCollectivityIdByMember)) {
                    stmtCollectivity.setObject(1, java.util.UUID.fromString(transaction.getMemberDebited().getId()));
                    try (ResultSet rs = stmtCollectivity.executeQuery()) {
                        if (!rs.next() || rs.getObject("collectivity_id") == null) {
                            throw new IllegalArgumentException("Cannot derive collectivity id from member " + transaction.getMemberDebited().getId());
                        }
                        collectivityId = rs.getObject("collectivity_id").toString();
                    }
                }

                statement.setObject(1, java.util.UUID.fromString(transaction.getId()));
                statement.setObject(2, java.util.UUID.fromString(collectivityId));
                statement.setObject(3, java.util.UUID.fromString(transaction.getMemberDebited().getId()));
                statement.setObject(4, java.util.UUID.fromString(transaction.getAccountCredited().getId()));
                statement.setBigDecimal(5, transaction.getAmount());
                statement.setString(6, transaction.getPaymentMode().name());
                statement.setDate(7, Date.valueOf(transaction.getCreationDate()));

                statement.executeUpdate();
                return transaction;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public CollectivityTransaction findById(String transactionId) {
       String sql = "SELECT id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date " +
                "FROM collectivity_transaction WHERE id = ?";
 
        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, java.util.UUID.fromString(transactionId));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (!resultSet.next()) {
                        return null;
                    }
                    return mapRow(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public List<CollectivityTransaction> findByCollectivityId(String collectivityId) {
        String sql = "SELECT id, collectivity_id, member_debited_id, account_credited_id, amount, payment_mode, creation_date " +
                "FROM collectivity_transaction WHERE collectivity_id = ? ORDER BY creation_date DESC";
 
        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setObject(1, java.util.UUID.fromString(collectivityId));
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
