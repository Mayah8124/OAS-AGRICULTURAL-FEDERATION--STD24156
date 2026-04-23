package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.Bank;
import org.ingredients.agriculturalfederation.entity.BankAccount;
import org.ingredients.agriculturalfederation.entity.CashAccount;
import org.ingredients.agriculturalfederation.entity.FinancialAccount;
import org.ingredients.agriculturalfederation.entity.MobileBankingAccount;
import org.ingredients.agriculturalfederation.entity.MobileBankingService;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Repository
public class JdbcCollectivityFinancialAccountRepository implements CollectivityFinancialAccountRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcCollectivityFinancialAccountRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public boolean collectivityExists(String collectivityId) {
        String sql = "SELECT 1 FROM collectivity WHERE id = ? LIMIT 1";
        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, collectivityId);
                try (ResultSet rs = statement.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public List<FinancialAccount> findFinancialAccountsByCollectivityAt(String collectivityId, LocalDate at) {
        String sql = "SELECT fa.id AS financial_account_id, fa.type AS financial_account_type, " +
                "fab.amount AS balance_amount, " +
                "mba.holder_name AS mba_holder_name, mba.mobile_banking_service AS mba_service, mba.mobile_number AS mba_number, " +
                "ba.holder_name AS ba_holder_name, ba.bank_name AS ba_bank_name, ba.bank_code AS ba_bank_code, ba.bank_branch_code AS ba_branch_code, " +
                "ba.bank_account_number AS ba_account_number, ba.bank_account_key AS ba_account_key " +
                "FROM collectivity_financial_account cfa " +
                "JOIN financial_account fa ON fa.id = cfa.financial_account_id " +
                "LEFT JOIN cash_account ca ON ca.id = fa.id " +
                "LEFT JOIN mobile_banking_account mba ON mba.id = fa.id " +
                "LEFT JOIN bank_account ba ON ba.id = fa.id " +
                "JOIN financial_account_balance fab ON fab.financial_account_id = fa.id AND fab.at_date = ? " +
                "WHERE cfa.collectivity_id = ?";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDate(1, java.sql.Date.valueOf(at));
                statement.setString(2, collectivityId);

                try (ResultSet rs = statement.executeQuery()) {
                    List<FinancialAccount> out = new ArrayList<>();

                    while (rs.next()) {
                        String id = rs.getObject("financial_account_id").toString();
                        String type = rs.getString("financial_account_type");

                        if (type == null) {
                            continue;
                        }

                        if ("CASH".equalsIgnoreCase(type)) {
                            CashAccount cashAccount = new CashAccount();
                            cashAccount.setId(id);
                            cashAccount.setAmount(rs.getBigDecimal("balance_amount"));
                            out.add(cashAccount);
                            continue;
                        }

                        if ("MOBILE_BANKING".equalsIgnoreCase(type)) {
                            MobileBankingAccount mobile = new MobileBankingAccount();
                            mobile.setId(id);
                            mobile.setHolderName(rs.getString("mba_holder_name"));
                            String service = rs.getString("mba_service");
                            mobile.setMobileBankingService(service == null ? null : MobileBankingService.valueOf(service));
                            mobile.setMobileNumber(rs.getString("mba_number"));
                            Number amount = (Number) rs.getObject("balance_amount");
                            mobile.setAmount(amount == null ? null : amount.doubleValue());
                            out.add(mobile);
                            continue;
                        }

                        if ("BANK".equalsIgnoreCase(type)) {
                            BankAccount bank = new BankAccount();
                            bank.setId(id);
                            bank.setHolderName(rs.getString("ba_holder_name"));
                            String bankName = rs.getString("ba_bank_name");
                            bank.setBankName(bankName == null ? null : Bank.valueOf(bankName));
                            Object bankCode = rs.getObject("ba_bank_code");
                            bank.setBankCode(bankCode == null ? null : ((Number) bankCode).intValue());
                            Object branchCode = rs.getObject("ba_branch_code");
                            bank.setBankBranchCode(branchCode == null ? null : ((Number) branchCode).intValue());
                            Object accNumber = rs.getObject("ba_account_number");
                            bank.setBankAccountNumber(accNumber == null ? null : ((Number) accNumber).intValue());
                            Object accKey = rs.getObject("ba_account_key");
                            bank.setBankAccountKey(accKey == null ? null : ((Number) accKey).intValue());
                            bank.setAmount(rs.getBigDecimal("balance_amount"));
                            out.add(bank);
                        }
                    }

                    return out;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }
}
