package com.leonidov.rest.controller;

import com.leonidov.rest.exception.ErrorResponse;
import com.leonidov.rest.model.NewUserPayload;
import com.leonidov.rest.model.User;
import com.leonidov.rest.service.UserService;
import jakarta.validation.Valid;
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

    private final UserService userService;
    private final MessageSource messageSource;

    public UserRestController(UserService userService,
                              MessageSource messageSource) {
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public ResponseEntity<List<User>> handleGetAllUsers() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.userService.findAll());
    }

    @PostMapping()
    @Transactional
    public ResponseEntity<?> handleAddNewUser(@Valid @RequestBody NewUserPayload payload,
            UriComponentsBuilder uriComponentsBuilder, Locale locale) {

        if (userService.findByUsername(payload.username()).isPresent())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.not_create_is_username_exists", new Object[0], locale))));

        User user = new User(payload.name(), payload.surname(),
                payload.username(), payload.password());

        userService.saveOrUpdate(user);

        return ResponseEntity.created(
                        uriComponentsBuilder.path("/api/users/{id}")
                            .build(Map.of("id", user.id())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> handleGetUser(@PathVariable UUID id) {
        return ResponseEntity.of(userService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> handleDeleteUser(@PathVariable UUID id, Locale locale) {

        if (userService.findById(id).isEmpty())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.find_by_id_not_exists", new Object[0], locale))));

        userService.deleteById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse("Успешно",
                        List.of(messageSource.getMessage(
                                "user.success.delete", new Object[0], locale))));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> handleUpdateUser(@PathVariable UUID id, @Valid @RequestBody NewUserPayload payload,
                                              UriComponentsBuilder uriComponentsBuilder, Locale locale) {
        Optional<User> user = userService.findById(id);

        if (user.isEmpty())
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ErrorResponse("Ошибка",
                            List.of(messageSource.getMessage(
                                    "user.errors.find_by_id_not_exists", new Object[0], locale))));

        User updatedUser = new User(user.get().id(), payload.name(), payload.surname(),
                payload.username(), payload.password());

        userService.saveOrUpdate(updatedUser);

        return ResponseEntity.created(
                        uriComponentsBuilder.path("/api/users/get/{id}")
                                .build(Map.of("id", updatedUser.id())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(updatedUser);
    }
}
