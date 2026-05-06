package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.ActivityAttendanceCount;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcCollectivityActivityAttendanceRepository implements CollectivityActivityAttendanceRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcCollectivityActivityAttendanceRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public List<ActivityAttendanceCount> getMemberAttendanceStats(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
            SELECT 
                m.id as member_id,
                COUNT(ca.id) as total_activities,
                COUNT(ama.id) as attended_activities
            FROM member m
            LEFT JOIN collectivity_activity ca ON ca.collectivity_id = m.collectivity_id 
                AND ca.executive_date BETWEEN ? AND ?
            LEFT JOIN activity_member_attendance ama ON ama.activity_id = ca.id 
                AND ama.member_id = m.id 
                AND ama.attendance_status = 'ATTENDED'
            WHERE m.collectivity_id = ?
            GROUP BY m.id
            ORDER BY m.id
            """;

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(from));
                stmt.setDate(2, java.sql.Date.valueOf(to));
                stmt.setString(3, collectivityId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    List<ActivityAttendanceCount> stats = new ArrayList<>();
                    while (rs.next()) {
                        String memberId = rs.getString("member_id");
                        long totalActivities = rs.getLong("total_activities");
                        long attendedActivities = rs.getLong("attended_activities");
                        
                        stats.add(new ActivityAttendanceCount(memberId, totalActivities, attendedActivities));
                    }
                    return stats;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving attendance statistics", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }
}
