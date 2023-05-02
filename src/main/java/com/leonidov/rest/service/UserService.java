package com.leonidov.rest.service;

import com.leonidov.rest.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> findAll();
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);
    void saveOrUpdate(User user);
    void deleteById(UUID id);

}
