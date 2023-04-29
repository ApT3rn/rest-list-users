CREATE TABLE t_users (
    id uuid PRIMARY KEY,
    c_name VARCHAR(32),
    c_surname VARCHAR(32),
    c_username VARCHAR(32),
    c_password VARCHAR(32) check ( length(trim(c_password)) > 8 )
);