package com.leonidov.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Sql("/sql/tasks_rest_controller/test_data.sql")
@Transactional
@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class UserRestControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void handleGetAllUsers_ReturnsValidResponseEntity() throws Exception {

        var requestBuilder = get("/api/users");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {
                                        "id": "1eacaeaa-42b4-490a-a2ef-d9afe8580bc9",
                                        "name": "name1",
                                        "surname": "surname1",
                                        "username": "username1",
                                        "password": "password1"
                                    },
                                    {
                                        "id": "2eacaeaa-42b4-490a-a2ef-d9afe8580bc9",
                                        "name": "name2",
                                        "surname": "surname2",
                                        "username": "username2",
                                        "password": "password2"                        
                                    }
                                ]
                                """)
                );
    }

    @Test
    void handleGetUser_PayloadIsValid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder = get("/api/users/1eacaeaa-42b4-490a-a2ef-d9afe8580bc9");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""                   
                                    {
                                        "id": "1eacaeaa-42b4-490a-a2ef-d9afe8580bc9",
                                        "name": "name1",
                                        "surname": "surname1",
                                        "username": "username1",
                                        "password": "password1"
                                    }
                                """)
                );
    }

    @Test
    void handleGetUser_PayloadInvalid_ReturnValidResponseEntity() throws Exception {

        var requestBuilder = get("/api/users/0eacaeaa-42b4-490a-a2ef-d9afe8580bc9");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(status().isNotFound());
    }

    @Test
    void handleAddNewUser_PayloadIsValid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                    "name": "name3",
                                    "surname": "surname3",
                                    "username": "username3",
                                    "password": "password3"
                                }
                                """
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "name": "name3",
                                    "surname": "surname3",
                                    "username": "username3",
                                    "password": "password3"
                                }
                                """),
                        jsonPath("$.id").exists()
                );
    }

    @Test
    void handleAddNewUser_PayloadInvalid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                                {
                                    "name": "name3",
                                    "surname": "surname3",
                                    "username": "",
                                    "password": "password3"
                                }
                                """
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Ошибка валидации",
                                    	"details": [
                                    		"Поле username не должно быть пустым"
                                    	]
                                }
                                """)
                );
    }

    @Test
    void handleDeleteUser_PayloadIsValid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder =
                delete("/api/users/1eacaeaa-42b4-490a-a2ef-d9afe8580bc9");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Успешно",
                                    	"details": [
                                    		"Пользователь успешно был удалён!"
                                    	]
                                }
                                """)
                );
    }

    @Test
    void handleDeleteUser_PayloadInvalid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder =
                delete("/api/users/1eacaeaa-42b4-490a-a2ef-d9afe8580bc0");

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Ошибка",
                                    	"details": [
                                    		"Пользователя с таким id не существует!"
                                    	]
                                }
                                """)
                );
    }

    @Test
    void handleUpdateUser_PayloadIsValid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/users/2eacaeaa-42b4-490a-a2ef-d9afe8580bc9")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                            {
                                "name": "name3",
                                "surname": "surname3",
                                "username": "usernamewwge",
                                "password": "password3"
                            }
                        """
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "id": "2eacaeaa-42b4-490a-a2ef-d9afe8580bc9",
                                    "name": "name3",
                                    "surname": "surname3",
                                    "username": "usernamewwge",
                                    "password": "password3"
                                }
                                """)
                );
    }

    @Test
    void handleUpdateUser_PayloadInvalid_ReturnValidResponseEntity() throws Exception {
        var requestBuilder = post("/api/users/1eacaeaa-42b4-490a-a2ef-d9afe8580bc0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                        """
                            {
                                "name": "name3",
                                "surname": "surname3",
                                "username": "username3",
                                "password": "password3"
                            }
                        """
                );

        this.mockMvc.perform(requestBuilder)
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                    "message": "Ошибка",
                                    	"details": [
                                    		"Пользователя с таким id не существует!"
                                    	]
                                }
                                """)
                );
    }
}