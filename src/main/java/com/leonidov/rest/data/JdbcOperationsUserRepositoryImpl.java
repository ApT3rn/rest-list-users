package com.leonidov.rest.data;

import com.leonidov.rest.model.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public JdbcOperationsUserRepositoryImpl(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Override
    public List<User> findAll() {
        return this.jdbcOperations.query("SELECT * FROM t_users", this);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return this.jdbcOperations.query("SELECT * FROM t_users WHERE id = ?",
                new Object[]{id}, this).stream().findFirst();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.jdbcOperations.query("SELECT * FROM t_users WHERE c_username = ?",
                new Object[]{username}, this).stream().findFirst();
    }

    @Override
    public void save(User user) {
        if (findById(user.id()).isPresent())
            this.jdbcOperations.update("""
                    UPDATE t_users SET c_name=?, c_surname=?, c_username=?, c_password=? WHERE id=?
                    """, new Object[]{user.name(), user.surname(), user.username(), user.password(), user.id()});
        else
            this.jdbcOperations.update("""
                        INSERT INTO t_users(id, c_name, c_surname, c_username, c_password) VALUES (?, ?, ?, ?, ?)
                    """, new Object[]{user.id(), user.name(), user.surname(), user.username(), user.password()});
    }

    @Override
    public void deleteById(UUID id) {
        this.jdbcOperations.update("""
                    DELETE FROM t_users WHERE id=?
                """, new Object[]{id});
    }

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(rs.getObject("id", UUID.class),
                rs.getString("c_name"), rs.getString("c_surname"),
                rs.getString("c_username"), rs.getString("c_password"));
    }
}
