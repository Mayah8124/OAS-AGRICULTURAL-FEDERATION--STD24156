package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.CollectivityActivity;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.ingredients.agriculturalfederation.entity.MonthlyRecurrenceRule;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcCollectivityActivityRepository implements CollectivityActivityRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcCollectivityActivityRepository(DataSourceConfig dataSourceConfig) {
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
    public List<CollectivityActivity> addActivities(String collectivityId, List<CollectivityActivity> activities) {
        if (activities == null || activities.isEmpty()) {
            return List.of();
        }

        String sqlInsertActivity = "INSERT INTO collectivity_activity (id, collectivity_id, label, activity_type, executive_date, recurrence_week_ordinal, recurrence_day_of_week) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String sqlInsertOccupation = "INSERT INTO collectivity_activity_occupation (activity_id, member_occupation) VALUES (?, ?) ON CONFLICT DO NOTHING";

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            connection.setAutoCommit(false);

            List<CollectivityActivity> out = new ArrayList<>();

            for (CollectivityActivity activity : activities) {
                if (activity.getId() == null || activity.getId().trim().isEmpty()) {
                    activity.setId(java.util.UUID.randomUUID().toString());
                }

                try (PreparedStatement stmt = connection.prepareStatement(sqlInsertActivity)) {
                    stmt.setString(1, activity.getId());
                    stmt.setString(2, collectivityId);
                    stmt.setString(3, activity.getLabel());
                    stmt.setString(4, activity.getActivityType());

                    if (activity.getExecutiveDate() == null) {
                        stmt.setNull(5, Types.DATE);
                    } else {
                        stmt.setDate(5, Date.valueOf(activity.getExecutiveDate()));
                    }

                    MonthlyRecurrenceRule recurrenceRule = activity.getRecurrenceRule();
                    if (recurrenceRule == null) {
                        stmt.setNull(6, Types.INTEGER);
                        stmt.setNull(7, Types.VARCHAR);
                    } else {
                        if (recurrenceRule.getWeekOrdinal() == null) {
                            stmt.setNull(6, Types.INTEGER);
                        } else {
                            stmt.setInt(6, recurrenceRule.getWeekOrdinal());
                        }
                        if (recurrenceRule.getDayOfWeek() == null) {
                            stmt.setNull(7, Types.VARCHAR);
                        } else {
                            stmt.setString(7, recurrenceRule.getDayOfWeek());
                        }
                    }

                    stmt.executeUpdate();
                }

                List<MemberOccupation> occupations = activity.getMemberOccupationConcerned();
                if (occupations != null && !occupations.isEmpty()) {
                    try (PreparedStatement stmtOcc = connection.prepareStatement(sqlInsertOccupation)) {
                        for (MemberOccupation occ : occupations) {
                            if (occ == null) {
                                continue;
                            }
                            stmtOcc.setString(1, activity.getId());
                            stmtOcc.setString(2, occ.name());
                            stmtOcc.addBatch();
                        }
                        stmtOcc.executeBatch();
                    }
                }

                out.add(activity);
            }

            connection.commit();
            return out;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Error adding collectivity activities", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                }
                dataSourceConfig.closeConnection(connection);
            }
        }
    }
}
