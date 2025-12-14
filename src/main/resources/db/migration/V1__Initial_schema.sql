------------------------------------------------------------
-- SCHEMA
------------------------------------------------------------
comment on schema public is 'standard public schema';
alter schema public owner to pg_database_owner;

------------------------------------------------------------
-- SEQUENCES
------------------------------------------------------------
create sequence color_id_seq as integer;
alter sequence color_id_seq owner to postgres;

create sequence inventory_id_seq as integer;
alter sequence inventory_id_seq owner to postgres;

create sequence item_id_seq as integer;
alter sequence item_id_seq owner to postgres;

create sequence photo_id_seq as integer;
alter sequence photo_id_seq owner to postgres;

create sequence size_id_seq as integer;
alter sequence size_id_seq owner to postgres;

create sequence user_role_id_seq as integer;
alter sequence user_role_id_seq owner to postgres;

create sequence users_id_seq;
alter sequence users_id_seq owner to postgres;

------------------------------------------------------------
-- TABLE: item
------------------------------------------------------------
create table item
(
    id integer not null default nextval('item_id_seq'),
    name varchar(200) not null,
    description varchar(1000),
    publish boolean default true not null,
    constraint item_pk primary key (id)
);

alter table item owner to postgres;

create unique index item_id_uindex on item (id);

------------------------------------------------------------
-- TABLE: size
------------------------------------------------------------
create table size
(
    id integer not null default nextval('size_id_seq'),
    name varchar(20) not null,
    constraint size_pk primary key (id)
);

alter table size owner to postgres;

create unique index size_id_uindex on size (id);

------------------------------------------------------------
-- TABLE: colors
------------------------------------------------------------
create table colors
(
    id integer not null default nextval('color_id_seq'),
    item_id integer not null
        constraint color_item_id_fk
            references item
            on update cascade on delete cascade,
    name varchar(40) not null,
    constraint color_pk primary key (id)
);

alter table colors owner to postgres;

create unique index color_id_uindex on colors (id);

------------------------------------------------------------
-- TABLE: inventory
------------------------------------------------------------
create table inventory
(
    id integer not null default nextval('inventory_id_seq'),
    stock_count integer default 0 not null,
    color_id integer not null
        constraint inventory_color_id_fk
            references colors
            on update cascade on delete cascade,
    size_id integer
        constraint inventory_size_id_fk
            references size,
    constraint inventory_pk primary key (id)
);

alter table inventory owner to postgres;

create unique index inventory_id_uindex on inventory (id);

------------------------------------------------------------
-- TABLE: photo
------------------------------------------------------------
create table photo
(
    id integer not null default nextval('photo_id_seq'),
    image bytea not null,
    color_id integer not null
        constraint photo_color_id_fk
            references colors
            on update cascade on delete cascade,
    constraint photo_pk primary key (id)
);

alter table photo owner to postgres;

create unique index photo_id_uindex on photo (id);

------------------------------------------------------------
-- TABLE: user_role
------------------------------------------------------------
create table user_role
(
    id integer not null default nextval('user_role_id_seq'),
    name varchar(50) not null,
    constraint user_role_pk primary key (id)
);

alter table user_role owner to postgres;

------------------------------------------------------------
-- PREDEFINED USER ROLES
------------------------------------------------------------
insert into user_role (id, name) values
                                     (1, 'Administrator'),
                                     (2, 'Manager'),
                                     (3, 'User');

-- синхронизация sequence с максимальным id
select setval('user_role_id_seq', (select max(id) from user_role));


create unique index user_role_id_uindex on user_role (id);
create unique index user_role_name_uindex on user_role (name);

------------------------------------------------------------
-- TABLE: users
------------------------------------------------------------
create table users
(
    id bigint not null default nextval('users_id_seq'),
    google_id varchar(255) not null
        constraint users_google_id_key unique,
    email varchar(255) not null
        constraint users_email_key unique,
    name varchar(255),
    picture text,
    role_id integer default 3
        constraint users_user_role_id_fk
            references user_role,
    image bytea,
    constraint users_pkey primary key (id)
);

alter table users owner to postgres;
