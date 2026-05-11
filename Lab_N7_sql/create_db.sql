--SET search_path TO s501787;

DROP TABLE IF EXISTS study_groups;
DROP TABLE IF EXISTS person;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS coordinates;
DROP TABLE IF EXISTS form_of_education;
DROP TABLE IF EXISTS country;

CREATE TABLE IF NOT EXISTS users 
(
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(128) NOT NULL 
);

CREATE TABLE IF NOT EXISTS coordinates 
(
    id SERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS form_of_education 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS semester_enum 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS country 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS person 
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    birthday TIMESTAMP NOT NULL,
    weight FLOAT CHECK (weight > 0),
    passport_id TEXT UNIQUE CHECK (LENGTH(passport_id) >= 8 OR passport_id IS NULL),
    country_id INTEGER REFERENCES country(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS study_groups 
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    coordinates_id INTEGER NOT NULL REFERENCES coordinates(id) ON DELETE CASCADE,
    creation_date TIMESTAMP DEFAULT NOW(),
    students_count INTEGER CHECK (students_count > 0),
    should_be_expelled INTEGER CHECK (should_be_expelled > 0),
    form_of_education_id INTEGER REFERENCES form_of_education(id),
    semester_enum_id INTEGER REFERENCES semester_enum(id),
    group_admin_id INTEGER REFERENCES person(id) ON DELETE CASCADE,
    owner_id INTEGER NOT NULL REFERENCES users(id) 
);