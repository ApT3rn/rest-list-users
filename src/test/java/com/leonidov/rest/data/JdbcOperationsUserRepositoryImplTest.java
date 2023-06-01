package com.leonidov.rest.data;

import com.leonidov.rest.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JdbcOperationsUserRepositoryImplTest {

    @Autowired
    JdbcOperationsUserRepository repository;

    @Test
    void findAll_ifDatabaseEmpty() {
        List<User> users = repository.findAll();

        assertEquals(0, users.size());
    }

    @Test
    void findAll_ifDatabaseNotEmpty() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        this.repository.save(user);
        List<User> users = this.repository.findAll();

        assertEquals(1, users.size());
        assertEquals(user.name(), users.get(0).name());
        this.repository.deleteById(user.id());
    }

    @Test
    void findById() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        this.repository.save(user);
        Optional<User> userFromDb = this.repository.findById(user.id());

        assertEquals(user, userFromDb.get());
        this.repository.deleteById(user.id());
    }

    @Test
    void findByUsername() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        this.repository.save(user);
        Optional<User> userFromDb = this.repository.findByUsername(user.username());

        assertEquals(user, userFromDb.get());
        this.repository.deleteById(user.id());
    }

    @Test
    void save() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        List<User> emptyList = this.repository.findAll();
        this.repository.save(user);
        List<User> userList = this.repository.findAll();

        assertEquals(0, emptyList.size());
        assertEquals(1, userList.size());
        assertEquals(user, userList.get(0));
        this.repository.deleteById(user.id());
    }

    @Test
    void update() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        this.repository.save(user);
        List<User> oldList = this.repository.findAll();
        User updateUser = new User(user.id(), "name2",
                "surname2", "username2", "password2");
        this.repository.update(updateUser);
        List<User> updatedList = this.repository.findAll();

        assertEquals(user, oldList.get(0));
        assertEquals(updateUser, updatedList.get(0));
        this.repository.deleteById(updateUser.id());
    }

    @Test
    void deleteById() {
        User user = new User(UUID.fromString("1eacaeaa-42b4-490a-a2ef-d9afe8580bc9"),
                "name1", "surname1", "username1", "password1");

        this.repository.save(user);
        List<User> oldList = this.repository.findAll();
        this.repository.deleteById(user.id());
        List<User> updatedList = this.repository.findAll();

        assertEquals(1, oldList.size());
        assertEquals(0, updatedList.size());
    }
}