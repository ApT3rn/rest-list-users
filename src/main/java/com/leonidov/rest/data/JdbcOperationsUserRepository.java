package com.leonidov.rest.data;

import com.leonidov.rest.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JdbcOperationsUserRepository {

    List<User> findAll();
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    void save(User user);
    void deleteById(UUID id);

}
