package com.leonidov.rest.data;

import com.leonidov.rest.model.User;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcOperationsUserRepositoryImpl implements JdbcOperationsUserRepository, RowMapper<User> {

    private final JdbcOperations jdbcOperations;

    public JdbcOperationsUserRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    private static final String SELECT_ALL_SQL = "SELECT * FROM t_users";
    private static final String SELECT_USER_BY_ID_SQL = "SELECT * FROM t_users WHERE id = ?";
    private static final String SELECT_USER_BY_USERNAME_SQL = "SELECT * FROM t_users WHERE c_username = ?";
    private static final String INSERT_USER_SQL = "INSERT INTO t_users(id, c_name, c_surname, c_username, c_password) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_USER_SQL = "UPDATE t_users SET c_name=?, c_surname=?, c_username=?, c_password=? WHERE id = ?";
    private static final String DELETE_USER_BY_ID_SQL = "DELETE FROM t_users WHERE id = ?";

    @Override
    public List<User> findAll() {
        return this.jdbcOperations.query(SELECT_ALL_SQL, this);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.jdbcOperations.query(SELECT_USER_BY_ID_SQL,
                new Object[]{id}, this).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.jdbcOperations.query(SELECT_USER_BY_USERNAME_SQL,
                new Object[]{username}, this).stream().findFirst();
    }

    @Override
    public void save(User user) {
            this.jdbcOperations.update(INSERT_USER_SQL, user.id(), user.name(),
                    user.surname(), user.username(), user.password());
    }

    @Override
    public void update(User user) {
        this.jdbcOperations.update(UPDATE_USER_SQL,
                user.name(), user.surname(), user.username(),
                user.password(), user.id());
    }

    @Override
    public void deleteById(UUID id) {
        this.jdbcOperations.update(DELETE_USER_BY_ID_SQL, id);
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getObject("id", UUID.class),
                rs.getString("c_name"), rs.getString("c_surname"),
                rs.getString("c_username"), rs.getString("c_password"));
    }
}
