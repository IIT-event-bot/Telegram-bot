create table roles
(
    id   serial primary key,
    name varchar(20) not null
);

create table users
(
    id       int primary key,
    username varchar(35)
);

create table groups
(
    id    serial primary key,
    title varchar(10) not null
);

create table students
(
    id         serial primary key,
    user_id    int unique,
    name       varchar(30) not null,
    surname    varchar(30) not null,
    patronymic varchar(30) not null,
    group_id   int,

    constraint fk_student_user
        foreign key (user_id)
            references users (id),
    constraint fk_student_group
        foreign key (group_id)
            references groups (id)
);

create table statements
(
    id         serial primary key,
    user_id    int,
    name       varchar(30) not null,
    surname    varchar(30) not null,
    patronymic varchar(30) not null,
    group_id   int,

    constraint fk_statement_user
        foreign key (user_id)
            references users (id),
    constraint fk_statement_group
        foreign key (group_id)
            references groups (id)
);

insert into roles(name)
VALUES ('USER');
insert into roles(name)
VALUES ('STUDENT');
insert into roles(name)
VALUES ('MANAGER');
insert into roles(name)
VALUES ('MAIN_ADMIN');