package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.Gender;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Repository
public class JdbcMemberRepository implements MemberRepository {

    private final DataSourceConfig dataSourceConfig;

    public JdbcMemberRepository(DataSourceConfig dataSourceConfig) {
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public void save(Member member) {
        String sql = "INSERT INTO member (id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation, collectivity_id, creation_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, member.getId());
            stmt.setString(2, member.getFirstName());
            stmt.setString(3, member.getLastName());
            if (member.getBirthDate() != null) { stmt.setDate(4, Date.valueOf(member.getBirthDate())); } else { stmt.setNull(4, Types.DATE); }
            stmt.setString(5, member.getGender() != null ? member.getGender().name() : null);
            stmt.setString(6, member.getAddress());
            stmt.setString(7, member.getProfession());
            stmt.setString(8, member.getPhoneNumber());
            stmt.setString(9, member.getEmail());
            stmt.setString(10, member.getOccupation() != null ? member.getOccupation().name() : null);
            stmt.setObject(11, null);
            stmt.setDate(12, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving member", e);
        }
    }

    @Override
    public void updateCollectivityId(String memberId, String collectivityId) {
        String sql = "UPDATE member SET collectivity_id = ? WHERE id = ?";
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectivityId);
            stmt.setString(2, memberId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating member collectivity_id", e);
        }
    }

    @Override
    public List<String> findRefereeIdsByMemberId(String memberId) {
        String sql = "SELECT referee_id FROM member_referee WHERE member_id = ?";
        List<String> refereeIds = new ArrayList<>();
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    refereeIds.add(rs.getString("referee_id"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding referee ids by member id", e);
        }
        return refereeIds;
    }

    @Override
    public Optional<Member> findById(String id) {
        String sql = "SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation FROM member WHERE id = ?";
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding member by id", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Member> findAllById(List<String> ids) {
        if (ids == null || ids.isEmpty())
            return List.of();

        StringBuilder sql = new StringBuilder(
                "SELECT id, first_name, last_name, birth_date, gender, address, profession, phone_number, email, occupation FROM member WHERE id IN (");
        for (int i = 0; i < ids.size(); i++) {
            sql.append("?");
            if (i < ids.size() - 1)
                sql.append(",");
        }
        sql.append(")");

        List<Member> members = new ArrayList<>();
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < ids.size(); i++) {
                stmt.setString(i + 1, ids.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(mapResultSetToMember(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members by ids", e);
        }
        return members;
    }

    @Override
    public void saveReferees(String memberId, List<String> refereeIds) {
        String sql = "INSERT INTO member_referee (member_id, referee_id) VALUES (?, ?)";
        try (Connection conn = dataSourceConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String refId : refereeIds) {
                stmt.setString(1, memberId);
                stmt.setString(2, refId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving referees", e);
        }
    }

    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getString("id"));
        member.setFirstName(rs.getString("first_name"));
        member.setLastName(rs.getString("last_name"));
        Date birthDate = rs.getDate("birth_date");
        member.setBirthDate(birthDate != null ? birthDate.toLocalDate() : null);
        member.setGender(parseGender(rs.getString("gender")));
        member.setAddress(rs.getString("address"));
        member.setProfession(rs.getString("profession"));
        member.setPhoneNumber(rs.getString("phone_number"));
        member.setEmail(rs.getString("email"));
        member.setOccupation(parseOccupation(rs.getString("occupation")));
        member.setReferees(new ArrayList<>());
        return member;
    }

    private static Gender parseGender(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return Gender.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static MemberOccupation parseOccupation(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return null;
        }
        try {
            return MemberOccupation.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
