CREATE SCHEMA IF NOT EXISTS custom_schema;

CREATE TABLE custom_schema.role
(
    role varchar(10) PRIMARY KEY
);

CREATE TABLE custom_schema.users
(
    id        bigserial PRIMARY KEY,
    username  varchar(25) UNIQUE                    NOT NULL,
    password  varchar(100)                          NOT NULL,
    role      varchar REFERENCES custom_schema.role NOT NULL,
    firstname varchar(25)                           NOT NULL,
    lastname  varchar(25)                           NOT NULL,
    email     varchar(25)                           NOT NULL
);

INSERT INTO custom_schema.role (role)
values ('ADMIN'),
       ('SUBSCRIBER'),
       ('JOURNALIST');

INSERT INTO custom_schema.users (username, password, role, firstname, lastname, email)
values ('leon', '$2a$10$N69ySDiY.93aieQw4eGl4.Q8EAmuri/u7QfZiWOaDcrY76aij9/r6', 'ADMIN', 'alexey', 'leonenko',
        'leshaleonenko@mail.ru'),
       ('maks', '$2a$10$N69ySDiY.93aieQw4eGl4.Q8EAmuri/u7QfZiWOaDcrY76aij9/r6', 'JOURNALIST', 'maksim', 'ivanov',
        'maksimivanov@gmail.com'),
       ('dima', '$2a$10$N69ySDiY.93aieQw4eGl4.Q8EAmuri/u7QfZiWOaDcrY76aij9/r6', 'SUBSCRIBER', 'dmitriy', 'petrov',
        'dimapetrov@gmail.com')