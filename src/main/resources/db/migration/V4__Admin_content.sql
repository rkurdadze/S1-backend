ALTER TABLE users
    ADD COLUMN IF NOT EXISTS status varchar(50),
    ADD COLUMN IF NOT EXISTS last_active timestamptz;

ALTER TABLE orders
    ADD COLUMN IF NOT EXISTS order_number varchar(50),
    ADD COLUMN IF NOT EXISTS status varchar(50),
    ADD COLUMN IF NOT EXISTS delivery_window varchar(120);

CREATE TABLE IF NOT EXISTS tag
(
    id   bigserial PRIMARY KEY,
    name varchar(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS item_tag
(
    item_id bigint NOT NULL REFERENCES item (id) ON DELETE CASCADE,
    tag_id  bigint NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (item_id, tag_id)
);

CREATE TABLE IF NOT EXISTS category
(
    id          bigserial PRIMARY KEY,
    title       varchar(200) NOT NULL,
    description text,
    highlight   varchar(200),
    items_count integer NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS news
(
    id      bigserial PRIMARY KEY,
    title   varchar(200) NOT NULL,
    date    varchar(100),
    summary text,
    image   text
);

CREATE TABLE IF NOT EXISTS collection
(
    id          bigserial PRIMARY KEY,
    title       varchar(200) NOT NULL,
    tag         varchar(100),
    description text,
    image       text,
    anchor      varchar(120)
);

CREATE TABLE IF NOT EXISTS editorial
(
    id      bigserial PRIMARY KEY,
    title   varchar(200) NOT NULL,
    summary text,
    image   text,
    cta     varchar(200)
);

CREATE TABLE IF NOT EXISTS promotion
(
    id       bigserial PRIMARY KEY,
    name     varchar(200) NOT NULL,
    scope    varchar(200),
    discount varchar(50),
    period   varchar(120),
    status   varchar(50)
);

CREATE TABLE IF NOT EXISTS delivery_zone
(
    id    bigserial PRIMARY KEY,
    zone  varchar(200) NOT NULL,
    price varchar(50),
    eta   varchar(120),
    notes text
);

CREATE TABLE IF NOT EXISTS newsletter_draft
(
    id         bigserial PRIMARY KEY,
    subject    text,
    message    text,
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS newsletter_segment
(
    id          bigserial PRIMARY KEY,
    name        varchar(200) NOT NULL,
    description text,
    count       integer NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS newsletter_send
(
    id         bigserial PRIMARY KEY,
    subject    text,
    message    text,
    sent_at    timestamptz NOT NULL DEFAULT now(),
    recipients varchar(50)
);

CREATE TABLE IF NOT EXISTS newsletter_send_segment
(
    send_id    bigint NOT NULL REFERENCES newsletter_send (id) ON DELETE CASCADE,
    segment_id bigint NOT NULL REFERENCES newsletter_segment (id) ON DELETE CASCADE,
    PRIMARY KEY (send_id, segment_id)
);
