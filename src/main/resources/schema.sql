CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL,

    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS item_requests
(
    item_request_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    requester_id    BIGINT       NOT NULL,
    description     VARCHAR(255) NOT NULL,

    CONSTRAINT fk_user FOREIGN KEY (requester_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    is_available BOOLEAN      NOT NULL,
    owner_id     BIGINT       NOT NULL,
    request_id   BIGINT UNIQUE,

    CONSTRAINT fk_user FOREIGN KEY (owner_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_request FOREIGN KEY (request_id) REFERENCES item_requests (item_request_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    text       VARCHAR(255) NOT NULL,
    item_id    BIGINT       NOT NULL,
    author_id  BIGINT       NOT NULL,

    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES users (user_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT                      NOT NULL,
    booker_id  BIGINT                      NOT NULL,
    status     VARCHAR(8)                  NOT NULL,

    CONSTRAINT fk_item FOREIGN KEY (item_id) REFERENCES items (item_id) ON DELETE CASCADE,
    CONSTRAINT fk_booker FOREIGN KEY (booker_id) REFERENCES users (user_id) ON DELETE CASCADE
)