CREATE TABLE IF NOT EXISTS uuid
(
    id               SERIAL              PRIMARY KEY,
    token            VARCHAR(350)        NOT NULL,
    expires_at       TIMESTAMP           NOT NULL
);

CREATE TABLE IF NOT EXISTS access_token
(
    id                SERIAL              PRIMARY KEY,
    token             VARCHAR(500)        NOT NULL,
    is_used           BIT                 NOT NULL,
    expires_at        TIMESTAMP           NOT NULL
);

CREATE TABLE IF NOT EXISTS jwt
(
    id                     SERIAL               PRIMARY KEY,
    token                  VARCHAR(1000)        NOT NULL,
    expires_at             TIMESTAMP            NOT NULL,
    access_token_id        INT                  NOT NULL,

    FOREIGN KEY (access_token_id) REFERENCES access_token ON DELETE CASCADE
);

