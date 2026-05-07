package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.ActivityAttendanceCount;
import org.ingredients.agriculturalfederation.entity.MemberFullStats;
import org.ingredients.agriculturalfederation.entity.OverallAttendanceStats;
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

    @Override
    public List<OverallAttendanceStats> getOverallAttendanceStats(LocalDate from, LocalDate to) {
        String sql = """
            SELECT 
                c.id as collectivity_id,
                COUNT(DISTINCT m.id) as total_members,
                COUNT(DISTINCT ca.id) as total_activities,
                COUNT(DISTINCT ama.id) as total_attended_activities
            FROM collectivity c
            LEFT JOIN member m ON m.collectivity_id = c.id
            LEFT JOIN collectivity_activity ca ON ca.collectivity_id = c.id 
                AND ca.executive_date BETWEEN ? AND ?
            LEFT JOIN activity_member_attendance ama ON ama.activity_id = ca.id 
                AND ama.attendance_status = 'ATTENDED'
            GROUP BY c.id
            ORDER BY c.id
            """;

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(from));
                stmt.setDate(2, java.sql.Date.valueOf(to));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    List<OverallAttendanceStats> stats = new ArrayList<>();
                    while (rs.next()) {
                        String collectivityId = rs.getString("collectivity_id");
                        long totalMembers = rs.getLong("total_members");
                        long totalActivities = rs.getLong("total_activities");
                        long totalAttendedActivities = rs.getLong("total_attended_activities");
                        
                        stats.add(new OverallAttendanceStats(collectivityId, totalMembers, totalActivities, totalAttendedActivities));
                    }
                    return stats;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving overall attendance statistics", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    @Override
    public List<MemberFullStats> getMemberFullStats(String collectivityId, LocalDate from, LocalDate to) {
        String sql = """
            SELECT 
                m.id as member_id,
                m.first_name,
                m.last_name,
                m.email,
                COALESCE(mp.total_earned, 0.0) as earned,
                COALESCE(mf.expected_amount, 0.0) as unpaid,
                CASE 
                    WHEN ca.total_activities = 0 THEN 0.0
                    ELSE (COALESCE(ama.attended_activities, 0.0) / ca.total_activities::numeric) * 100.0
                END as attendance_rate
            FROM member m
            LEFT JOIN (
                SELECT member_id, SUM(amount) as total_earned
                FROM member_payment 
                WHERE creation_date BETWEEN ? AND ?
                GROUP BY member_id
            ) mp ON mp.member_id = m.id
            LEFT JOIN (
                SELECT 
                    m.id as member_id,
                    SUM(CASE 
                        WHEN mf.eligible_from <= ? AND mf.eligible_from IS NOT NULL
                        THEN CASE mf.frequency
                            WHEN 'WEEKLY' THEN (? - GREATEST(mf.eligible_from, ?)) / 7 + 1
                            WHEN 'MONTHLY' THEN (? - GREATEST(mf.eligible_from, ?)) / 30 + 1
                            WHEN 'ANNUALLY' THEN (? - GREATEST(mf.eligible_from, ?)) / 365 + 1
                            WHEN 'PUNCTUALLY' THEN 1
                            ELSE 0
                        END * COALESCE(mf.amount, 0)
                        ELSE 0
                    END) as expected_amount
                FROM member m
                LEFT JOIN membership_fee mf ON mf.collectivity_id = m.collectivity_id
                WHERE m.collectivity_id = ?
                GROUP BY m.id
            ) mf ON mf.member_id = m.id
            LEFT JOIN (
                SELECT 
                    m.id as member_id,
                    COUNT(DISTINCT ca.id) as total_activities,
                    COUNT(DISTINCT ama.id) as attended_activities
                FROM member m
                LEFT JOIN collectivity_activity ca ON ca.collectivity_id = m.collectivity_id 
                    AND ca.executive_date BETWEEN ? AND ?
                LEFT JOIN activity_member_attendance ama ON ama.activity_id = ca.id 
                    AND ama.member_id = m.id 
                    AND ama.attendance_status = 'ATTENDED'
                WHERE m.collectivity_id = ?
                GROUP BY m.id
            ) ca ON ca.member_id = m.id
            LEFT JOIN (
                SELECT 
                    m.id as member_id,
                    COUNT(DISTINCT ama.id) as attended_activities
                FROM member m
                LEFT JOIN collectivity_activity ca ON ca.collectivity_id = m.collectivity_id 
                    AND ca.executive_date BETWEEN ? AND ?
                LEFT JOIN activity_member_attendance ama ON ama.activity_id = ca.id 
                    AND ama.member_id = m.id 
                    AND ama.attendance_status = 'ATTENDED'
                WHERE m.collectivity_id = ?
                GROUP BY m.id
            ) ama ON ama.member_id = m.id
            WHERE m.collectivity_id = ?
            ORDER BY m.id
            """;

        try (Connection connection = dataSourceConfig.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            
            // Paramètres pour les paiements
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            // Paramètres pour les cotisations attendues
            stmt.setDate(3, java.sql.Date.valueOf(to));
            stmt.setDate(4, java.sql.Date.valueOf(to));
            stmt.setDate(5, java.sql.Date.valueOf(from));
            stmt.setDate(6, java.sql.Date.valueOf(to));
            stmt.setDate(7, java.sql.Date.valueOf(from));
            stmt.setDate(8, java.sql.Date.valueOf(to));
            stmt.setDate(9, java.sql.Date.valueOf(from));
            stmt.setString(10, collectivityId);
            
            // Paramètres pour les activités
            stmt.setDate(11, java.sql.Date.valueOf(from));
            stmt.setDate(12, java.sql.Date.valueOf(to));
            stmt.setString(13, collectivityId);
            
            // Paramètres pour les activités assistées
            stmt.setDate(14, java.sql.Date.valueOf(from));
            stmt.setDate(15, java.sql.Date.valueOf(to));
            stmt.setString(16, collectivityId);
            
            // Paramètre final pour le filtre principal
            stmt.setString(17, collectivityId);
                
            try (ResultSet rs = stmt.executeQuery()) {
                List<MemberFullStats> stats = new ArrayList<>();
                while (rs.next()) {
                    String memberId = rs.getString("member_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    String email = rs.getString("email");
                    double earned = rs.getDouble("earned");
                    double unpaid = rs.getDouble("unpaid");
                    double attendanceRate = rs.getDouble("attendance_rate");
                    
                    stats.add(new MemberFullStats(memberId, firstName, lastName, email, earned, Math.max(0.0, unpaid - earned), attendanceRate));
                }
                return stats;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving member full statistics", e);
        }
    }
}
