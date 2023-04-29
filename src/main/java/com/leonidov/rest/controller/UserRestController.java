package com.leonidov.rest.controller;

import com.leonidov.rest.data.JdbcOperationsUserRepository;
import com.leonidov.rest.exception.ErrorResponse;
import com.leonidov.rest.model.NewUserPayload;
import com.leonidov.rest.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final JdbcOperationsUserRepository jdbcOperationsUserRepository;
    private final MessageSource messageSource;

    @Autowired
    public UserRestController(JdbcOperationsUserRepository jdbcOperationsUserRepository,
                              MessageSource messageSource) {
        this.jdbcOperationsUserRepository = jdbcOperationsUserRepository;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<User>> handleGetAllUsers() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.jdbcOperationsUserRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> handleGetUser(@PathVariable UUID id) {
        return ResponseEntity.of(jdbcOperationsUserRepository.findById(id));
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<?> handleAddNewUser(@Valid @RequestBody NewUserPayload payload,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        if (jdbcOperationsUserRepository.findByUsername(payload.username()).isPresent())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.not_create_is_username_exists", new Object[0], locale))));

        User user = new User(payload.name(), payload.surname(),
                payload.username(), payload.password());

        jdbcOperationsUserRepository.save(user);

        return ResponseEntity.created(
                        uriComponentsBuilder.path("/api/users/{id}")
                            .build(Map.of("id", user.id())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(user);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> handleDeleteUser(@PathVariable UUID id, Locale locale) {

        if (jdbcOperationsUserRepository.findById(id).isEmpty())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.find_by_id_not_exists", new Object[0], locale))));

        jdbcOperationsUserRepository.deleteById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Успешно",
                        List.of(messageSource.getMessage(
                                "user.success.delete", new Object[0], locale))));
    }

    @PostMapping("/{id}")
    @Transactional
    public ResponseEntity<?> handleUpdateUser(@PathVariable UUID id, @Valid @RequestBody NewUserPayload payload,
                                              UriComponentsBuilder uriComponentsBuilder, Locale locale) {
        Optional<User> user = jdbcOperationsUserRepository.findById(id);

        if (user.isEmpty())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.find_by_id_not_exists", new Object[0], locale))));

        User updatedUser = new User(user.get().id(), payload.name(), payload.surname(),
                payload.username(), payload.password());

        jdbcOperationsUserRepository.save(updatedUser);

        return ResponseEntity.created(
                        uriComponentsBuilder.path("/api/users/get/{id}")
                                .build(Map.of("id", updatedUser.id())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser);
    }
}
