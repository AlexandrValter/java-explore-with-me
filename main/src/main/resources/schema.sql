CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    name  VARCHAR(50) NOT NULL,
    UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(50),
    UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS locations
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    latitude  numeric,
    longitude numeric,
    UNIQUE (latitude, longitude)
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(500),
    category_id        BIGINT
        constraint events_category_id
            references categories
            on update cascade on delete cascade,
    description        VARCHAR(1000),
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    event_date         TIMESTAMP WITHOUT TIME ZONE,
    initiator_id       BIGINT
        CONSTRAINT events_user_id
            REFERENCES users
            ON UPDATE CASCADE ON DELETE CASCADE,
    location_id        BIGINT
        CONSTRAINT events_location_id
            REFERENCES locations
            ON UPDATE CASCADE ON DELETE CASCADE,
    paid               BOOLEAN,
    participant_limit  INT,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOLEAN,
    state              VARCHAR(100),
    title              VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN,
    title  VARCHAR(200)
);

CREATE TABLE IF NOT EXISTS events_compilations
(
    compilation_id BIGINT
        constraint events_compilations_comp_id
            references compilations
            ON UPDATE CASCADE ON DELETE CASCADE,
    event_id       BIGINT
        constraint events_compilations_event_id
            references events
            ON UPDATE CASCADE ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE,
    event_id     BIGINT
        constraint request_event_id
            references events
            ON UPDATE CASCADE ON DELETE CASCADE,
    requester_id BIGINT
        constraint request_user_id
            references users
            ON UPDATE CASCADE ON DELETE CASCADE,
    status       VARCHAR(50),
    UNIQUE (event_id, requester_id)
);