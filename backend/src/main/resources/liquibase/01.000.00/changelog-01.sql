-- liquibase formatted sql

-- changeset karmanchikova-ev:1-1
create table status
(
    id   bigserial primary key,
    code text not null unique,
    name text not null
);

create table document
(
    id           bigserial primary key,
    type         text not null,
    organization text not null,
    date         timestamp,
    patient      text not null,
    description  text,
    status_id    bigint
        references status
);

insert into status(id, code, name)
values (1, 'NEW', 'Новый'),
       (2, 'IN_PROCESS', 'В обработке'),
       (3, 'ACCEPTED', 'Принят'),
       (4, 'REJECTED', 'Отклонен');

insert into document (type, organization, date, patient, description, status_id)
values ('Направление на медико-социальную экспертизу организацией, оказывающей медицинскую помощь',
        'ОГБУЗ Саянская городская больница', '2023-11-04 22:30:06.000000', 'Иванов Иван Иванович',
        'Направление', '1'),
       ('Направление на медико-социальную экспертизу организацией, оказывающей медицинскую помощь',
        'ОГБУЗ Саянская городская больница', '2023-11-04 22:30:06.000000', 'Иванов Иван Михайлович',
        'Направление', '1');

create table outbox_document
(
    id          bigserial primary key,
    message     text not null,
    send        bool      default false,
    accepted    bool,
    create_date timestamp default current_timestamp
);

create table outbox_message
(
    id          bigserial primary key,
    message     text not null,
    send        bool      default false,
    accepted    bool,
    create_date timestamp default current_timestamp
);