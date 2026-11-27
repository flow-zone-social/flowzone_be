-- Ví tài chính của user: lưu sẵn số dư để đọc nhanh, không phải SUM lại toàn bộ giao dịch.
-- Nguồn sự thật vẫn là fin_transactions; balance chỉ là bản cache, luôn có thể tính lại từ đó.
-- Mỗi user đúng 1 ví (UNIQUE user_id). Tách khỏi bảng users để module transaction
-- không phải ghi vào entity của module user (tránh bị save() profile ghi đè balance).

ALTER TABLE fin_wallets
add column version BIGINT default 0
