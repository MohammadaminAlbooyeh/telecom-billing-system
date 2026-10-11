-- V8: add password, first_name, last_name to subscribers
ALTER TABLE subscribers ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'changeme';
ALTER TABLE subscribers ADD COLUMN first_name VARCHAR(100) NOT NULL DEFAULT '';
ALTER TABLE subscribers ADD COLUMN last_name VARCHAR(100) NOT NULL DEFAULT '';

UPDATE subscribers SET
    first_name = split_part(name, ' ', 1),
    last_name = COALESCE(NULLIF(split_part(name, ' ', 2), ''), 'User'),
    password = '$2a$10$DowJpK8VYRZ8vKxj8vKxjOe8vKxj8vKxj8vKxj8vKxj8vKxj8vKxj8'
WHERE first_name = '' OR last_name = '' OR password = 'changeme';

ALTER TABLE subscribers ALTER COLUMN first_name DROP DEFAULT;
ALTER TABLE subscribers ALTER COLUMN last_name DROP DEFAULT;
ALTER TABLE subscribers ALTER COLUMN password DROP DEFAULT;

ALTER TABLE subscribers DROP COLUMN name;