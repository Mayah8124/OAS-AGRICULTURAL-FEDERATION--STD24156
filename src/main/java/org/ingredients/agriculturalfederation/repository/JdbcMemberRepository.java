package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.Gender;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcMemberRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public void save(Member member) {
        String sql = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, UUID.fromString(member.getId()));
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            stmt.setDate(4, Date.valueOf(member.getBirthDate()));
            stmt.setString(5, member.getGender().name());
            stmt.setString(6, member.getAddress());
            stmt.setString(7, member.getProfession());
            stmt.setString(8, member.getPhoneNumber());
            stmt.setString(9, member.getEmail());
            stmt.setString(10, member.getOccupation().name());
            stmt.setObject(11, null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    @Override
    public void updateCollectivityId(String memberId, String collectivityId) {
        String sql = "UPDATE member SET collectivity_id = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, UUID.fromString(collectivityId));
            stmt.setObject(2, UUID.fromString(memberId));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating member collectivity_id", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    @Override
    public List<String> findRefereeIdsByMemberId(String memberId) {
        String sql = "SELECT referee_id FROM member_referee WHERE member_id = ?";
        List<String> refereeIds = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, UUID.fromString(memberId));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                refereeIds.add(rs.getObject("referee_id").toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding referee ids by member id", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return refereeIds;
    }

    @Override
    public Optional<Member> findById(String id) {
        String sql = "SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation FROM member WHERE id = ?";
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setObject(1, UUID.fromString(id));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by id", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public List<Member> findAllById(List<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        
        StringBuilder sql = new StringBuilder("SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation FROM member WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1) sql.append(",");
        }
        sql.append(")");

        List<Member> members = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < ids.size(); i++) {
                stmt.setObject(i + 1, UUID.fromString(ids.get(i)));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                members.add(mapResultSetToMember(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members by ids", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return members;
    }

    @Override
    public void saveReferees(String memberId, List<String> refereeIds) {
        String sql = "INSERT INTO member_referee (member_id, referee_id) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String refId : refereeIds) {
                stmt.setObject(1, UUID.fromString(memberId));
                stmt.setObject(2, UUID.fromString(refId));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving referees", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getObject("id").toString());
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        member.setBirthDate(rs.getDate("birth_date").toLocalDate());
        member.setGender(Gender.valueOf(rs.getString("gender")));
        member.setAddress(rs.getString("address"));
        member.setProfession(rs.getString("profession"));
        member.setPhoneNumber(rs.getString("phone_number"));
        member.setEmail(rs.getString("email"));
        member.setOccupation(MemberOccupation.valueOf(rs.getString("occupation")));
        member.setReferees(new ArrayList<>());
        return member;
    }
}
