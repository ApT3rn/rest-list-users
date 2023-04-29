package com.leonidov.rest.controller;

import com.leonidov.rest.data.JdbcOperationsUserRepository;
import com.leonidov.rest.exception.ErrorResponse;
import com.leonidov.rest.model.NewUserPayload;
import com.leonidov.rest.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestControllerTest {

    @Mock
    JdbcOperationsUserRepository jdbcOperationsUserRepository;

    @Mock
    MessageSource messageSource;

    @InjectMocks
    UserRestController controller;


    @Test
    @DisplayName("GET /api/users возвращает ответ со статусом 200 OK и списком пользователей")
    void handleGetAllUsers_ReturnsValidResponseEntity() {

        var users = List.of(new User(UUID.randomUUID(), "name1", "surname1", "username1", "password1"),
                    new User(UUID.randomUUID(), "name2", "surname2", "username2", "password2"));
        doReturn(users).when(this.jdbcOperationsUserRepository).findAll();
        var responseEntity = this.controller.handleGetAllUsers();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(users, responseEntity.getBody());
    }

    @Test
    @DisplayName("GET /api/users/{id} возвращает ответ со статусом 200 OK и пользователем")
    void handleGetUser_IdIsValid_ReturnValidResponse() {
        var id = UUID.randomUUID();
        var user = new User(id, "name", "surname", "username", "password");

        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.of(user));
        var responseEntity = this.controller.handleGetUser(id);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(user, responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("GET /api/users/{id} возвращает ответ со статусом 404 NOT_FOUND и пустым значением, при условии что пользователя с таким id не существует")
    void handleGetUser_IdInvalid_ReturnValidResponse() {
        var id = UUID.randomUUID();

        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.empty());
        var responseEntity = this.controller.handleGetUser(id);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("POST /api/users/add Возвращает ответ со статусом 201 CREATED и добавляет нового пользователя")
    void handleAddNewUser_PayloadIsValid_ReturnValidResponseEntity() {

        var name = "name";
        var username = "username";

        when(this.jdbcOperationsUserRepository.findByUsername(username)).thenReturn(Optional.empty());
        var responseEntity = this.controller.handleAddNewUser(
                new NewUserPayload(name, "surname", username, "password"),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), Locale.ENGLISH);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        if (responseEntity.getBody() instanceof User user) {
            assertNotNull(user.id());
            assertEquals(name, user.name());
            assertEquals(username, user.username());

            assertEquals(URI.create("http://localhost:8080/api/users/" + user.id()),
                    responseEntity.getHeaders().getLocation());

            verify(this.jdbcOperationsUserRepository, times(1)).findByUsername(username);
            verify(this.jdbcOperationsUserRepository, times(1)).save(user);
        } else {
            assertInstanceOf(User.class, responseEntity.getBody());
        }
    }

    @Test
    @DisplayName("POST /api/users/add Возвращает ответ со статусом 400 BAD_REQUEST при условии что пользователь с таким username уже существует")
    void handleAddNewUser_PayloadIsInvalid_ReturnValidResponseEntity() {

        var username = "username";
        var locale = Locale.US;
        var errorMessage = "Пользователь с таким username уже существует!";

        doReturn(errorMessage).when(this.messageSource).getMessage(
                "user.errors.not_create_is_username_exists", new Object[0], locale);
        when(this.jdbcOperationsUserRepository.findByUsername(username)).thenReturn(Optional.of(new User("", "", "", "")));
        var responseEntity = this.controller.handleAddNewUser(
                new NewUserPayload("name", "surname", username, "password"),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorResponse("Ошибка", List.of(errorMessage)), responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findByUsername(username);
    }

    @Test
    @DisplayName("DELETE /api/users/delete/{id} Возвращает ответ со статусом 200 OK и удаляет пользователя")
    void handleDeleteUser_IdIsValid_ReturnValidResponse() {

        var id = UUID.randomUUID();
        var locale = Locale.ENGLISH;
        var errorMessage = "Пользователь успешно был удалён!";

        doReturn(errorMessage).when(this.messageSource).getMessage(
                "user.success.delete", new Object[0], locale);
        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.of(new User("", "", "", "")));
        var responseEntity = this.controller.handleDeleteUser(id, locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorResponse("Успешно", List.of(errorMessage)), responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
        verify(this.jdbcOperationsUserRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE /api/users/delete/{id} Возвращает ответ со статусом 400 BAD_REQUEST, при условии что пользователя с таким id не существует")
    void handleDeleteUser_IdInvalid_ReturnValidResponse() {

        var id = UUID.randomUUID();
        var locale = Locale.ENGLISH;
        var errorMessage = "Пользователя с таким id не существует!";

        doReturn(errorMessage).when(this.messageSource).getMessage(
                "user.errors.find_by_id_not_exists", new Object[0], locale);
        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.empty());
        var responseEntity = this.controller.handleDeleteUser(id, locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorResponse("Ошибка", List.of(errorMessage)), responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("POST /api/users/update/{id} Возвращает ответ со статусом 201 CREATED")
    void handleUpdateUser_IdIsValid_ReturnValidResponse() {

        var id = UUID.randomUUID();
        var locale = Locale.ENGLISH;
        var user = new User(id, "name", "surname", "username", "password");

        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.of(user));
        var responseEntity = this.controller.handleUpdateUser(
                id, new NewUserPayload("name", "surname", "username", "password"),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(user, responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
        verify(this.jdbcOperationsUserRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("POST /api/users/update/{id} Возвращает ответ со статусом 400 BAD_REQUEST, при условии что пользователя с таким id не существует")
    void handleUpdateUser_IdInvalid_ReturnValidResponse() {

        var id = UUID.randomUUID();
        var locale = Locale.ENGLISH;
        var errorMessage = "Пользователя с таким id не существует!";

        doReturn(errorMessage).when(this.messageSource).getMessage(
                "user.errors.find_by_id_not_exists", new Object[0], locale);
        when(this.jdbcOperationsUserRepository.findById(id)).thenReturn(Optional.empty());
        var responseEntity = this.controller.handleUpdateUser(
                id, new NewUserPayload("name", "surname", "username", "password"),
                UriComponentsBuilder.fromUriString("http://localhost:8080"), locale);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, responseEntity.getHeaders().getContentType());
        assertEquals(new ErrorResponse("Ошибка", List.of(errorMessage)), responseEntity.getBody());
        verify(this.jdbcOperationsUserRepository, times(1)).findById(id);
    }
}