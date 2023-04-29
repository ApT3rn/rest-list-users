package com.leonidov.rest.model;

import java.util.UUID;


public record User(UUID id, String name, String surname, String username, String password) {

    public User(String name, String surname, String username, String password) {
        this(UUID.randomUUID(), name, surname, username, password);
    }
}
