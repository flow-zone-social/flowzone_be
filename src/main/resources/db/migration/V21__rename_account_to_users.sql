-- Đổi tên bảng account -> users (entity Account -> User).
-- Dùng "users" thay vì "user" vì USER là từ khóa dành riêng của PostgreSQL.
-- Postgres tự cập nhật mọi FK đang trỏ tới bảng (follow, auth_provider, token, refresh_token,
-- conversation*, message, faq_view, attachments, ...), không cần drop/recreate constraint.

ALTER TABLE account RENAME TO users;

-- PK + index: đổi tên cho nhất quán (đổi tên index backing PK sẽ đổi luôn tên constraint).
ALTER INDEX IF EXISTS account_pkey          RENAME TO users_pkey;
ALTER INDEX IF EXISTS idx_account_created   RENAME TO idx_users_created;
ALTER INDEX IF EXISTS ux_account_username   RENAME TO ux_users_username;

-- Cột account_id -> user_id (entity Token.user, RefreshToken.userId).
ALTER TABLE token         RENAME COLUMN account_id TO user_id;
ALTER TABLE refresh_token RENAME COLUMN account_id TO user_id;

ALTER INDEX IF EXISTS idx_refresh_account_expiry RENAME TO idx_refresh_user_expiry;

ALTER TABLE auth_provider
    RENAME CONSTRAINT uk_auth_provider_account_provider TO uk_auth_provider_user_provider;

-- Tên FK mặc định còn chứa "account".
ALTER TABLE users         RENAME CONSTRAINT account_role_id_fkey          TO users_role_id_fkey;
ALTER TABLE token         RENAME CONSTRAINT token_account_id_fkey         TO token_user_id_fkey;
ALTER TABLE refresh_token RENAME CONSTRAINT refresh_token_account_id_fkey TO refresh_token_user_id_fkey;
ALTER TABLE token         RENAME CONSTRAINT token_account_id_key          TO token_user_id_key;
