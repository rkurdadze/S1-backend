CREATE TABLE IF NOT EXISTS share_link
(
    token        varchar(36) PRIMARY KEY,
    platform     varchar(50),
    destination  varchar(50),
    caption      text,
    url          text                       NOT NULL,
    images       text,
    color_name   varchar(100),
    item_name    varchar(200),
    description  text,
    price        numeric(12, 2),
    item_id      bigint,
    created_at   timestamptz                NOT NULL DEFAULT now(),
    expires_at   timestamptz,
    click_count  bigint                     NOT NULL DEFAULT 0
);
