-- Hashtag giờ extends AbstractEntity (id, created_by, updated_by) thay vì PK = name.
-- name giữ lại như cột unique để syncCounts()/deleteUnused() vẫn match theo tag text.

ALTER TABLE hashtag DROP CONSTRAINT hashtag_pkey;

ALTER TABLE hashtag
    ADD COLUMN id         VARCHAR(36),
    ADD COLUMN created_by VARCHAR(36),
    ADD COLUMN updated_by VARCHAR(36);

UPDATE hashtag SET id = gen_random_uuid()::text WHERE id IS NULL;

ALTER TABLE hashtag
    ALTER COLUMN id SET NOT NULL,
    ADD CONSTRAINT hashtag_pkey PRIMARY KEY (id);

ALTER TABLE hashtag
    ADD CONSTRAINT uk_hashtag_name UNIQUE (name);
