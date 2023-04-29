package com.leonidov.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewUserPayload(@NotBlank(message = "Поле name не должно быть пустым")
                             @Size(min = 0, max = 32, message = "Длина поля name должно быть не более 32 символов") String name,
                             @NotBlank(message = "Поле surname не должно быть пустым")
                             @Size(min = 0, max = 32, message = "Длина поля surname должно быть не более 32 символов") String surname,
                             @NotBlank(message = "Поле username не должно быть пустым")
                             @Size(min = 0, max = 32, message = "Длина поля username должно быть не более 32 символов") String username,
                             @NotBlank(message = "Поле password не должно быть пустым")
                             @Size(min = 8, max = 32, message = "Пароль должен иметь длину от 8 до 32 символов") String password) {
}
