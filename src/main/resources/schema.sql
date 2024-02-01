CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    password CHAR(60) NOT NULL,
    CONSTRAINT pk_id PRIMARY KEY (id),
    CONSTRAINT uq_username UNIQUE (username)
);