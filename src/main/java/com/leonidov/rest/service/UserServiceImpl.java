package com.leonidov.rest.service;

import com.leonidov.rest.data.JdbcOperationsUserRepository;
import com.leonidov.rest.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final JdbcOperationsUserRepository jdbcOperationsUserRepository;

    public UserServiceImpl(JdbcOperationsUserRepository jdbcOperationsUserRepository) {
        this.jdbcOperationsUserRepository = jdbcOperationsUserRepository;
    }

    @Override
    public List<User> findAll() {
        return jdbcOperationsUserRepository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jdbcOperationsUserRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcOperationsUserRepository.findByUsername(username);
    }

    @Override
    public void saveOrUpdate(User user) {
        if (findById(user.id()).isPresent())
            jdbcOperationsUserRepository.update(user);
        else
            jdbcOperationsUserRepository.save(user);
    }

    @Override
    public void deleteById(UUID id) {
        jdbcOperationsUserRepository.deleteById(id);
    }
}
