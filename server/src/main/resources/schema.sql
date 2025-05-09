CREATE TABLE IF NOT EXISTS users
(
    user_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    name
    VARCHAR
(
    150
) NOT NULL,
    email VARCHAR
(
    150
) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE
(
    email
)
    );

CREATE TABLE IF NOT EXISTS item_requests
(
    item_request_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    requester_id
    BIGINT
    NOT
    NULL,
    description
    VARCHAR
(
    500
) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_item_request FOREIGN KEY
(
    requester_id
) REFERENCES users
(
    user_id
)
                      ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS items
(
    item_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    name
    VARCHAR
(
    100
) NOT NULL,
    description VARCHAR
(
    255
) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT UNIQUE,
    CONSTRAINT fk_user_item FOREIGN KEY
(
    owner_id
) REFERENCES users
(
    user_id
) ON DELETE CASCADE,
    CONSTRAINT fk_request_item FOREIGN KEY
(
    request_id
) REFERENCES item_requests
(
    item_request_id
)
  ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS comments
(
    comment_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    text
    VARCHAR
(
    500
) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    CONSTRAINT fk_item_comment FOREIGN KEY
(
    item_id
) REFERENCES items
(
    item_id
) ON DELETE CASCADE,
    CONSTRAINT fk_author_comment FOREIGN KEY
(
    author_id
) REFERENCES users
(
    user_id
)
  ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id
    BIGINT
    GENERATED
    BY
    DEFAULT AS
    IDENTITY
    NOT
    NULL
    PRIMARY
    KEY,
    start_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    end_date
    TIMESTAMP
    WITHOUT
    TIME
    ZONE
    NOT
    NULL,
    item_id
    BIGINT
    NOT
    NULL,
    booker_id
    BIGINT
    NOT
    NULL,
    status
    VARCHAR
(
    8
) NOT NULL,
    CONSTRAINT fk_item_booking FOREIGN KEY
(
    item_id
) REFERENCES items
(
    item_id
) ON DELETE CASCADE,
    CONSTRAINT fk_booker_booking FOREIGN KEY
(
    booker_id
) REFERENCES users
(
    user_id
)
  ON DELETE CASCADE
    )