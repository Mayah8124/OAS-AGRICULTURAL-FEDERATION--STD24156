package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.CollectivityTransaction;
import org.ingredients.agriculturalfederation.entity.CashAccount;
import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.entity.Gender;
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
        String sql = "SELECT ct.id, ct.collectivity_id, ct.account_credited_id, ct.amount, ct.payment_mode, ct.creation_date, " +
                "m.id as m_id, m.first_name, m.last_name, m.birth_date, m.gender, m.address, m.profession, " +
                "m.phone_number, m.email, m.occupation " +
                "FROM collectivity_transaction ct " +
                "LEFT JOIN member m ON m.id = ct.member_debited_id " +
                "WHERE ct.collectivity_id = ? AND ct.creation_date >= ? AND ct.creation_date <= ? " +
                "ORDER BY ct.creation_date DESC";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, collectivityId);
                statement.setDate(2, Date.valueOf(fromDate));
                statement.setDate(3, Date.valueOf(toDate));

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<CollectivityTransaction> transactions = new ArrayList<>();
                    while (resultSet.next()) {
                        transactions.add(mapRowWithMemberJoin(resultSet));
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
                statement.setString(1, collectivityId);
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
                    stmtCollectivity.setString(1, transaction.getMemberDebited().getId());
                    try (ResultSet rs = stmtCollectivity.executeQuery()) {
                        if (!rs.next() || rs.getObject("collectivity_id") == null) {
                            throw new IllegalArgumentException("Cannot derive collectivity id from member " + transaction.getMemberDebited().getId());
                        }
                        collectivityId = rs.getObject("collectivity_id").toString();
                    }
                }

                statement.setString(1, transaction.getId());
                statement.setString(2, collectivityId);
                statement.setString(3, transaction.getMemberDebited().getId());
                statement.setString(4, transaction.getAccountCredited().getId());
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
                statement.setString(1, transactionId);
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
                statement.setString(1, collectivityId);
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

    private static CollectivityTransaction mapRowWithMemberJoin(ResultSet resultSet) throws SQLException {
        CollectivityTransaction transaction = new CollectivityTransaction();
        transaction.setId(resultSet.getString("id"));
        transaction.setCreationDate(resultSet.getDate("creation_date").toLocalDate());
        transaction.setAmount(resultSet.getBigDecimal("amount"));

        String paymentMode = resultSet.getString("payment_mode");
        transaction.setPaymentMode(paymentMode == null ? null : PaymentMode.valueOf(paymentMode));

        String accountCreditedId = resultSet.getString("account_credited_id");
        if (accountCreditedId != null) {
            FinancialAccount accountCredited = new CashAccount();
            accountCredited.setId(accountCreditedId);
            transaction.setAccountCredited(accountCredited);
        }

        String memberId = resultSet.getString("m_id");
        if (memberId != null) {
            Member member = new Member();
            member.setId(memberId);
            member.setFirstName(resultSet.getString("first_name"));
            member.setLastName(resultSet.getString("last_name"));
            Date birthDate = resultSet.getDate("birth_date");
            member.setBirthDate(birthDate != null ? birthDate.toLocalDate() : null);
            String gender = resultSet.getString("gender");
            try { member.setGender(gender != null ? Gender.valueOf(gender) : null); } catch (IllegalArgumentException ignored) {}
            member.setAddress(resultSet.getString("address"));
            member.setProfession(resultSet.getString("profession"));
            member.setPhoneNumber(resultSet.getString("phone_number"));
            member.setEmail(resultSet.getString("email"));
            member.setReferees(new java.util.ArrayList<>());
            transaction.setMemberDebited(member);
        }

        return transaction;
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
