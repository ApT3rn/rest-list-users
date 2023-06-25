## REST-приложение списка пользователей

Простое приложение для хранения пользователей в базе данных h2.

## HTTP-запросы

- GET /api/users - получить всех пользователей;
- POST /api/users - добавить нового пользователя;
- GET /api/users/{id} - получить пользователя по id;
- PUT /api/users/{id} - обновить пользователя по id;
- DELETE /api/users/{id} - удалить пользователя по id.

## Стек:
Java 17, Spring Framework (Boot, Web, Data-JDBC), h2, flyway.
