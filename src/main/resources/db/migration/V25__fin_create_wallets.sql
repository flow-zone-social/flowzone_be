-- Ví tài chính của user: lưu sẵn số dư để đọc nhanh, không phải SUM lại toàn bộ giao dịch.
-- Nguồn sự thật vẫn là fin_transactions; balance chỉ là bản cache, luôn có thể tính lại từ đó.
-- Mỗi user đúng 1 ví (UNIQUE user_id). Tách khỏi bảng users để module transaction
-- không phải ghi vào entity của module user (tránh bị save() profile ghi đè balance).

CREATE TABLE fin_wallets (
    id         VARCHAR(36) PRIMARY KEY,
    user_id    VARCHAR(36)    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    balance    NUMERIC(19, 2) NOT NULL DEFAULT 0,
    created_by VARCHAR(36),
    updated_by VARCHAR(36),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP,
    CONSTRAINT uk_fin_wallets_user UNIQUE (user_id)
);

-- Điền sẵn số dư cho các user đã có giao dịch: INCOME cộng, EXPENSE trừ.
-- User chưa có giao dịch sẽ được tạo ví lúc phát sinh giao dịch đầu tiên.
INSERT INTO fin_wallets (id, user_id, balance, created_at)
SELECT gen_random_uuid()::text,
       user_id,
       SUM(CASE type WHEN 'INCOME' THEN amount WHEN 'EXPENSE' THEN -amount ELSE 0 END),
       now()
FROM fin_transactions
GROUP BY user_id;
