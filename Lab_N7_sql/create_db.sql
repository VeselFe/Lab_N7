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

CREATE TABLE IF NOT EXISTS form_of_education 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS semester 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS country 
(
    id SERIAL PRIMARY KEY,
    name VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS coordinates 
(
    id SERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS person 
(
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    birthday TIMESTAMP NOT NULL,
    weight FLOAT CHECK (weight > 0),
    passport VARCHAR(14) UNIQUE CHECK (passport IS NULL OR LENGTH(passport) = 0 OR LENGTH(passport) >= 8),
    country_id INTEGER REFERENCES country(id) NOT NULL
);

CREATE TABLE IF NOT EXISTS study_groups 
(
    id BIGSERIAL PRIMARY KEY,
    key BIGINT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    coordinates_id INTEGER NOT NULL REFERENCES coordinates(id) ON DELETE CASCADE,
    creation_date TIMESTAMP DEFAULT NOW(),
    students_count INTEGER CHECK (students_count > 0),
    should_be_expelled INTEGER CHECK (should_be_expelled > 0),
    form_of_education_id INTEGER REFERENCES form_of_education(id),
    semester_id INTEGER REFERENCES semester(id),
    group_admin_id INTEGER REFERENCES person(id) ON DELETE CASCADE,
    owner_id INTEGER NOT NULL REFERENCES users(id) 
);

CREATE OR REPLACE FUNCTION delete_with_group() 
RETURNS TRIGGER AS $$
BEGIN
    DELETE FROM coordinates WHERE id = OLD.coordinates_id;
    DELETE FROM person WHERE id = OLD.group_admin_id;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_delete_group
AFTER DELETE ON study_groups
FOR EACH ROW
EXECUTE FUNCTION delete_with_group();