-- Thêm tính năng quản lý thu nhập, quản lý tiền ra vào của mỗi người dùng
--   fin_categories   : hạng mục thu nhập (cha)
--   fin_products     : phân loại chi tiết thuộc 1 hạng mục (con)
--   fin_transactions : lịch sử giao dịch của user, gắn với 1 product
-- Quy ước giống V7/V8: id/FK VARCHAR(36), datetime TIMESTAMP, đủ 4 cột audit của AbstractEntity.

CREATE TABLE fin_categories (
    id           VARCHAR(36) PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    descriptions TEXT,
    avatar_url   TEXT,
    created_by   VARCHAR(36),
    updated_by   VARCHAR(36),
    created_at   TIMESTAMP NOT NULL DEFAULT now(),
    updated_at   TIMESTAMP,
    CONSTRAINT uk_fin_categories_name UNIQUE (name)
);

CREATE TABLE fin_products (
    id              VARCHAR(36) PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    descriptions    TEXT,
    avatar_url      TEXT,
    fin_category_id VARCHAR(36) NOT NULL REFERENCES fin_categories(id),
    created_by      VARCHAR(36),
    updated_by      VARCHAR(36),
    created_at      TIMESTAMP NOT NULL DEFAULT now(),
    updated_at      TIMESTAMP,
    CONSTRAINT uk_fin_products_category_name UNIQUE (fin_category_id, name)
);

CREATE TABLE fin_transactions (
    id             VARCHAR(36) PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    descriptions   TEXT,
    avatar_url     TEXT,
    amount         NUMERIC(19, 2) NOT NULL,
    type           VARCHAR(16) NOT NULL,
    is_blocked     BOOLEAN NOT NULL DEFAULT FALSE,
    fin_product_id VARCHAR(36) NOT NULL REFERENCES fin_products(id),
    user_id        VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_by     VARCHAR(36),
    updated_by     VARCHAR(36),
    created_at     TIMESTAMP NOT NULL DEFAULT now(),
    updated_at     TIMESTAMP
);

-- Truy vấn chính: lịch sử giao dịch của 1 user, mới nhất trước.
CREATE INDEX idx_fin_transactions_user_created ON fin_transactions(user_id, created_at DESC);
CREATE INDEX idx_fin_transactions_product      ON fin_transactions(fin_product_id);
-- FK không tự có index trong Postgres: cần cho join/lọc products theo category.
-- (đã được phủ bởi uk_fin_products_category_name vì fin_category_id là cột đầu của unique)
