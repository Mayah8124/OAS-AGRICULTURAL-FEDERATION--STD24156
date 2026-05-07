package org.ingredients.agriculturalfederation.repository;

import org.ingredients.agriculturalfederation.config.DataSourceConfig;
import org.ingredients.agriculturalfederation.entity.Collectivity;
import org.ingredients.agriculturalfederation.entity.CollectivityStructure;
import org.ingredients.agriculturalfederation.entity.Gender;
import org.ingredients.agriculturalfederation.entity.Member;
import org.ingredients.agriculturalfederation.entity.MemberOccupation;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityIdentityAlreadyAssignedException;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityIdentityConflictException;
import org.ingredients.agriculturalfederation.validator.exception.CollectivityNotFoundException;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcCollectivityRepository implements CollectivityRepository {

    private final DataSourceConfig dataSourceConfig;
    private final MemberRepository memberRepository;

    public JdbcCollectivityRepository(DataSourceConfig dataSourceConfig, MemberRepository memberRepository) {
        this.dataSourceConfig = dataSourceConfig;
        this.memberRepository = memberRepository;
    }

    @Override
    public void save(Collectivity collectivity, boolean federationApproval) {
        String sqlCollectivity = "INSERT INTO collectivity (id, location, federation_approval) VALUES (?, ?, ?)";
        String sqlStructure = "INSERT INTO collectivity_structure (collectivity_id, president_member_id, vice_president_member_id, treasurer_member_id, secretary_member_id) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtColl = conn.prepareStatement(sqlCollectivity)) {
                stmtColl.setString(1, collectivity.getId());
                stmtColl.setString(2, collectivity.getLocation());
                stmtColl.setBoolean(3, federationApproval);
                stmtColl.executeUpdate();
            }

            try (PreparedStatement stmtStr = conn.prepareStatement(sqlStructure)) {
                stmtStr.setString(1, collectivity.getId());
                stmtStr.setString(2, collectivity.getStructure().getPresident().getId());
                stmtStr.setString(3, collectivity.getStructure().getVicePresident().getId());
                stmtStr.setString(4, collectivity.getStructure().getTreasurer().getId());
                stmtStr.setString(5, collectivity.getStructure().getSecretary().getId());
                stmtStr.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new RuntimeException("Error saving collectivity and structure", e);
        } finally {
            if (conn != null) {
                dataSourceConfig.closeConnection(conn);
            }
        }
    }

    @Override
    public Optional<Collectivity> findById(String id) {
        String sql = "SELECT c.id, c.location, c.name, c.number, cs.president_member_id, cs.vice_president_member_id, cs.treasurer_member_id, cs.secretary_member_id " +
                "FROM collectivity c " +
                "JOIN collectivity_structure cs ON c.id = cs.collectivity_id " +
                "WHERE c.id = ?";

        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Collectivity collectivity = new Collectivity();
                collectivity.setId(rs.getObject("id").toString());
                collectivity.setLocation(rs.getString("location"));
                collectivity.setName(rs.getString("name"));
                Object numberObj = rs.getObject("number");
                collectivity.setNumber(numberObj == null ? null : ((Number) numberObj).intValue());

                Member president = memberRepository.findById(rs.getObject("president_member_id").toString()).orElse(null);
                Member vicePresident = memberRepository.findById(rs.getObject("vice_president_member_id").toString()).orElse(null);
                Member treasurer = memberRepository.findById(rs.getObject("treasurer_member_id").toString()).orElse(null);
                Member secretary = memberRepository.findById(rs.getObject("secretary_member_id").toString()).orElse(null);

                collectivity.setStructure(new CollectivityStructure(president, vicePresident, treasurer, secretary));
                collectivity.setMembers(findMembersByCollectivityId(id));

                return Optional.of(collectivity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return Optional.empty();
    }

    @Override
    public void assignIdentity(String collectivityId, String name, Integer number) {
        String sqlSelect = "SELECT name, number FROM collectivity WHERE id = ?";
        String sqlUpdate = "UPDATE collectivity SET name = ?, number = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();

            try (PreparedStatement stmtSelect = conn.prepareStatement(sqlSelect)) {
                stmtSelect.setString(1, collectivityId);
                ResultSet rs = stmtSelect.executeQuery();
                if (!rs.next()) {
                    throw new CollectivityNotFoundException("Collectivity with ID " + collectivityId + " not found");
                }

                String currentName = rs.getString("name");
                Object currentNumber = rs.getObject("number");
                if (currentName != null || currentNumber != null) {
                    throw new CollectivityIdentityAlreadyAssignedException("Collectivity identity is already assigned");
                }
            }

            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setString(1, name);
                if (number == null) {
                    stmtUpdate.setNull(2, Types.INTEGER);
                } else {
                    stmtUpdate.setInt(2, number);
                }
                stmtUpdate.setString(3, collectivityId);
                stmtUpdate.executeUpdate();
            }
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new CollectivityIdentityConflictException("Collectivity name or number already exists");
            }
            throw new RuntimeException("Error assigning collectivity identity", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    @Override
    public void linkMembers(String collectivityId, List<String> memberIds) {
        String sql = "INSERT INTO collectivity_member (collectivity_id, member_id) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String memberId : memberIds) {
                stmt.setString(1, collectivityId);
                stmt.setString(2, memberId);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Error linking members to collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
    }

    private List<Member> findMembersByCollectivityId(String collectivityId) {
        String sql = "SELECT member_id FROM collectivity_member WHERE collectivity_id = ?";
        List<String> memberIds = new ArrayList<>();
        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, collectivityId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                memberIds.add(rs.getObject("member_id").toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding members for collectivity", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return memberRepository.findAllById(memberIds);
    }

    public Optional<Collectivity> findByIdWithMembers(String id) {
        String sql = """
                        SELECT c.id, c.location, c.name, c.number,
                        cs.president_member_id, cs.vice_president_member_id, cs.treasurer_member_id, cs.secretary_member_id,
                        m.id as member_id, m.first_name, m.last_name, m.birth_date, m.gender, m.address, m.profession,
                        m.phone_number, m.email, m.occupation
                        FROM collectivity c
                        LEFT JOIN collectivity_structure cs ON c.id = cs.collectivity_id
                        LEFT JOIN collectivity_member cm ON c.id = cm.collectivity_id
                        LEFT JOIN member m ON cm.member_id = m.id
                        WHERE c.id = ?
                    """;

        Connection conn = null;
        try {
            conn = dataSourceConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            
            Collectivity collectivity = null;
            List<Member> members = new ArrayList<>();
            
            while (rs.next()) {
                if (collectivity == null) {
                    collectivity = new Collectivity();
                    collectivity.setId(rs.getObject("id").toString());
                    collectivity.setLocation(rs.getString("location"));
                    collectivity.setName(rs.getString("name"));
                    Object numberObj = rs.getObject("number");
                    collectivity.setNumber(numberObj == null ? null : ((Number) numberObj).intValue());
                    
                    String presidentId = rs.getObject("president_member_id") != null ?
                        rs.getObject("president_member_id").toString() : null;
                    String vicePresidentId = rs.getObject("vice_president_member_id") != null ? 
                        rs.getObject("vice_president_member_id").toString() : null;
                    String treasurerId = rs.getObject("treasurer_member_id") != null ? 
                        rs.getObject("treasurer_member_id").toString() : null;
                    String secretaryId = rs.getObject("secretary_member_id") != null ? 
                        rs.getObject("secretary_member_id").toString() : null;
                    
                    Member president = presidentId != null ? memberRepository.findById(presidentId).orElse(null) : null;
                    Member vicePresident = vicePresidentId != null ? memberRepository.findById(vicePresidentId).orElse(null) : null;
                    Member treasurer = treasurerId != null ? memberRepository.findById(treasurerId).orElse(null) : null;
                    Member secretary = secretaryId != null ? memberRepository.findById(secretaryId).orElse(null) : null;
                    
                    collectivity.setStructure(new CollectivityStructure(president, vicePresident, treasurer, secretary));
                }
                
                String memberId = rs.getObject("member_id") != null ? rs.getObject("member_id").toString() : null;
                if (memberId != null) {
                    Member member = new Member();
                    member.setId(memberId);
                    member.setFirstName(rs.getString("first_name"));
                    member.setLastName(rs.getString("last_name"));
                    member.setBirthDate(rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null);
                    member.setGender(rs.getString("gender") != null ? Gender.valueOf(rs.getString("gender")) : null);
                    member.setAddress(rs.getString("address"));
                    member.setProfession(rs.getString("profession"));
                    member.setPhoneNumber(rs.getString("phone_number"));
                    member.setEmail(rs.getString("email"));
                    member.setOccupation(rs.getString("occupation") != null ? MemberOccupation.valueOf(rs.getString("occupation")) : null);
                    
                    members.add(member);
                }
            }
            
            if (collectivity != null) {
                collectivity.setMembers(members);
                return Optional.of(collectivity);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding collectivity with members", e);
        } finally {
            dataSourceConfig.closeConnection(conn);
        }
        return Optional.empty();
    }
}
