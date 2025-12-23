CREATE TABLE IF NOT EXISTS item_tag
(
    item_id bigint NOT NULL REFERENCES item (id) ON DELETE CASCADE,
    tag_id  bigint NOT NULL REFERENCES tag (id) ON DELETE CASCADE,
    PRIMARY KEY (item_id, tag_id)
);

CREATE INDEX IF NOT EXISTS idx_item_tag_item_id ON item_tag (item_id);
CREATE INDEX IF NOT EXISTS idx_item_tag_tag_id ON item_tag (tag_id);
