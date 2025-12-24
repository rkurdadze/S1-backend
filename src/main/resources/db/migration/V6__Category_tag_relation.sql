CREATE TABLE IF NOT EXISTS category_tag
(
    category_id bigint NOT NULL REFERENCES category (id) ON DELETE CASCADE,
    tag_id      bigint NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (category_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_category_tag_category_id ON category_tag (category_id);
CREATE INDEX IF NOT EXISTS idx_category_tag_tag_id ON category_tag (tag_id);
