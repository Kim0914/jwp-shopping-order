package cart.dao;

import cart.domain.Member;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class MemberDao {

    private final JdbcTemplate jdbcTemplate;

    public MemberDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Member> getMemberById(Long id) {
        String sql = "SELECT member.id, member.email, member.password, member_point.point " +
                "FROM member " +
                "JOIN member_point ON member_point.member_id = member.id " +
                "WHERE member.id = ? ";
        try {
            Member member = jdbcTemplate.queryForObject(sql, new MemberRowMapper(), id);
            return Optional.ofNullable(member);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<Member> getMemberByEmail(String email) {
        String sql = "SELECT member.id, member.email, member.password, member_point.point " +
                "FROM member " +
                "JOIN member_point ON member_point.member_id = member.id " +
                "WHERE member.email = ? ";
        try {
            Member member = jdbcTemplate.queryForObject(sql, new MemberRowMapper(), email);
            return Optional.ofNullable(member);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void addMember(Member member) {
        String sql = "INSERT INTO member (email, password) VALUES (?, ?)";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword());
    }

    public void updateMember(Member member) {
        String sql = "UPDATE member SET email = ?, password = ? WHERE id = ?";
        jdbcTemplate.update(sql, member.getEmail(), member.getPassword(), member.getId());
    }

    public void deleteMember(Long id) {
        String sql = "DELETE FROM member WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Member> getAllMembers() {
        String sql = "SELECT member.id, member.email, member.password, member_point.point " +
                "FROM member " +
                "JOIN member_point ON member_point.member_id = member.id ";

        return jdbcTemplate.query(sql, new MemberRowMapper());
    }

    private static class MemberRowMapper implements RowMapper<Member> {
        @Override
        public Member mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Member(rs.getLong("member.id"), rs.getString("member.email"), rs.getString("member.password"), rs.getLong("member_point.point"));
        }
    }
}

