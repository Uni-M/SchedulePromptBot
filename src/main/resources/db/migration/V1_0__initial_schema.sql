CREATE TABLE IF NOT EXISTS users
(
    id          bigserial primary key deferrable,
    user_name   varchar unique,
    first_name  varchar,
    second_name varchar,
    chat_id     bigint unique,
    time_zone   varchar
);
CREATE INDEX IF NOT EXISTS users_chat_id_idx ON users (chat_id);

CREATE TABLE IF NOT EXISTS prompts
(
    id                 bigserial primary key,
    user_name          varchar,
    task_description   varchar unique,
    reminding_date     timestamptz,
    reminder_frequency varchar,
    state              varchar,
    CONSTRAINT user_name_fk FOREIGN KEY (user_name) REFERENCES users (user_name) ON UPDATE CASCADE
);
CREATE INDEX IF NOT EXISTS prompts_task_description_idx ON prompts (task_description);

CREATE TABLE IF NOT EXISTS chat_state
(
    id        bigserial primary key,
    user_name varchar,
    state     varchar
);
CREATE INDEX IF NOT EXISTS chat_state_id_idx ON chat_state (user_name);

INSERT INTO users (user_name, first_name, second_name)
VALUES ('JuliMelnikova', 'Julia', 'Melnikova');
INSERT INTO users (user_name, first_name, second_name)
VALUES ('AnnaMaika', 'Anna', 'Kulikova');
INSERT INTO users (user_name, first_name, second_name)
VALUES ('Lama', 'Katya', 'Melnikova');
INSERT INTO users (user_name, first_name, second_name)
VALUES ('Test', 'Name', 'Surname');
INSERT INTO users (user_name, first_name, second_name)
VALUES ('Qwe', 'qwe', 'qwert');