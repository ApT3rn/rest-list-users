package com.leonidov.rest.service;

import com.leonidov.rest.data.JdbcOperationsUserRepository;
import com.leonidov.rest.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    JdbcOperationsUserRepository repository;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    void findAll() {
        when(this.repository.findAll()).thenReturn(Collections.emptyList());
        List<User> users = this.userService.findAll();

        assertEquals(0, users.size());
    }

    @Test
    void findById() {
        User user = new User("n", "s", "u", "p");

        when(this.repository.findById(user.id())).thenReturn(Optional.of(user));
        Optional<User> request = this.userService.findById(user.id());

        assertEquals(user, request.get());
    }

    @Test
    void findByUsername() {
        User user = new User("n", "s", "u", "p");

        when(this.repository.findByUsername(user.username())).thenReturn(Optional.of(user));
        Optional<User> request = this.userService.findByUsername(user.username());

        assertEquals(user, request.get());
    }

    @Test
    void saveOrUpdate_ifUserExists() {
        User user = new User("n", "s", "u", "p");

        when(this.repository.findById(user.id())).thenReturn(Optional.of(user));
        this.userService.saveOrUpdate(user);

        verify(this.repository, times(1)).update(user);
        verify(this.repository, times(0)).save(user);
    }

    @Test
    void saveOrUpdate_ifUserNotExists() {
        User user = new User("n", "s", "u", "p");

        when(this.repository.findById(user.id())).thenReturn(Optional.empty());
        this.userService.saveOrUpdate(user);

        verify(this.repository, times(0)).update(user);
        verify(this.repository, times(1)).save(user);
    }

    @Test
    void deleteById() {
        UUID id = UUID.randomUUID();

        this.userService.deleteById(id);

        verify(this.repository, times(1)).deleteById(id);
    }
}