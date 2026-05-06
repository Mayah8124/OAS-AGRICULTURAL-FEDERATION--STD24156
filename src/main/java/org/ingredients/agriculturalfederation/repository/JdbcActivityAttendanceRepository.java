package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.dto.response.ActivityMemberAttendance;
import org.ingredients.agriculturalfederation.dto.response.MemberDescription;
import org.ingredients.agriculturalfederation.entity.AttendanceStatus;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcActivityAttendanceRepository implements ActivityAttendanceRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcActivityAttendanceRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public boolean activityExists(String activityId) {
        String sql = "SELECT 1 FROM collectivity_activity WHERE id = ? LIMIT 1";
        Connection connection = null;

        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, activityId);
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
    public List<ActivityMemberAttendance> getActivityAttendance(String collectivityId, String activityId) {
        String sql = """
            SELECT DISTINCT 
                m.id as member_id,
                m.first_name,
                m.last_name,
                m.email,
                m.occupation,
                COALESCE(aa.attendance_status, 'UNDEFINED') as attendance_status,
                cao.member_occupation as activity_occupation
            FROM member m
            LEFT JOIN activity_attendance aa ON m.id = aa.member_id AND aa.activity_id = ?
            LEFT JOIN collectivity_activity cao ON ? = cao.id
            WHERE (m.collectivity_id = ? OR aa.member_id IS NOT NULL)
            ORDER BY m.last_name, m.first_name
            """;

        Connection connection = null;
        try {
            connection = dataSourceConfig.getConnection();
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, activityId);
                stmt.setString(2, activityId);
                stmt.setString(3, collectivityId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    List<ActivityMemberAttendance> attendanceList = new ArrayList<>();
                    
                    while (rs.next()) {
                        String memberId = rs.getString("member_id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        String email = rs.getString("email");
                        String occupation = rs.getString("occupation");
                        String attendanceStatusStr = rs.getString("attendance_status");
                        String activityOccupation = rs.getString("activity_occupation");
                        
                        // Determine attendance status based on business rules
                        AttendanceStatus attendanceStatus = determineAttendanceStatus(
                            attendanceStatusStr, 
                            occupation, 
                            activityOccupation,
                            collectivityId
                        );
                        
                        MemberDescription memberDescription = MemberDescription.builder()
                                .id(memberId)
                                .firstName(firstName)
                                .lastName(lastName)
                                .email(email)
                                .occupation(occupation)
                                .build();
                        
                        ActivityMemberAttendance attendance = ActivityMemberAttendance.builder()
                                .id(memberId + "_" + activityId)
                                .memberDescription(memberDescription)
                                .attendanceStatus(attendanceStatus)
                                .build();
                        
                        attendanceList.add(attendance);
                    }
                    
                    return attendanceList;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving activity attendance", e);
        } finally {
            dataSourceConfig.closeConnection(connection);
        }
    }

    private AttendanceStatus determineAttendanceStatus(String attendanceStatusStr, 
                                                       String memberOccupation,
                                                       String activityOccupation,
                                                       String collectivityId) {
        // Parse existing attendance status
        AttendanceStatus existingStatus = null;
        if (attendanceStatusStr != null) {
            try {
                existingStatus = AttendanceStatus.valueOf(attendanceStatusStr);
            } catch (IllegalArgumentException e) {
                existingStatus = null;
            }
        }
        
        // If member is outside collectivity or not concerned by activity occupation, only ATTENDED is possible
        if (activityOccupation == null || !isMemberConcerned(memberOccupation, activityOccupation)) {
            return existingStatus == AttendanceStatus.ATTENDED ? AttendanceStatus.ATTENDED : AttendanceStatus.UNDEFINED;
        }
        
        // For concerned members inside collectivity, return existing status or UNDEFINED
        return existingStatus != null ? existingStatus : AttendanceStatus.UNDEFINED;
    }

    private boolean isMemberConcerned(String memberOccupation, String activityOccupation) {
        if (memberOccupation == null || activityOccupation == null) {
            return false;
        }
        
        try {
            MemberOccupation memberOcc = MemberOccupation.valueOf(memberOccupation);
            MemberOccupation activityOcc = MemberOccupation.valueOf(activityOccupation);
            return memberOcc == activityOcc;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
