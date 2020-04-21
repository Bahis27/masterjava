DROP TABLE IF EXISTS users;
DROP SEQUENCE IF EXISTS user_seq;
DROP TABLE IF EXISTS projectgroups;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS cities;
DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS group_type;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');

CREATE TYPE group_type AS ENUM ('finished', 'current');

CREATE TABLE cities (
    id          TEXT PRIMARY KEY NOT NULL,
    name        TEXT NOT NULL
);

CREATE TABLE projects (
    id          TEXT PRIMARY KEY NOT NULL,
    description TEXT not null
);

CREATE TABLE projectgroups (
    id          TEXT PRIMARY KEY NOT NULL,
    type        group_type NOT NULL,
    project_id  TEXT NOT NULL,
    FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE SEQUENCE user_seq START 100000;
CREATE TABLE users (
  id            INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name     TEXT NOT NULL,
  email         TEXT NOT NULL,
  flag          user_flag NOT NULL,
  city_id       TEXT NOT NULL,
  FOREIGN KEY (city_id) REFERENCES cities (id)
);
CREATE UNIQUE INDEX email_idx ON users (email);


-- populate --

insert into cities (id, name) values ('spb', 'Санкт-Петербург');
insert into cities (id, name) values ('mow', 'Москва');
insert into cities (id, name) values ('kiv', 'Киев');
insert into cities (id, name) values ('mnsk', 'Минск');

insert into projects (id, description) values ('masterjava', 'Masterjava');
insert into projects (id, description) values ('topjava', 'Topjava');