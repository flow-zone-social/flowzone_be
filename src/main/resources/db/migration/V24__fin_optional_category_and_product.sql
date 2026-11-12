-- Product có thể không thuộc category; transaction có thể không gắn product.
--
-- 1) fin_products: chủ không còn suy ra được từ category (category có thể NULL) -> thêm user_id riêng.
-- 2) Hai FK trở thành tùy chọn và dùng ON DELETE SET NULL: xóa category/product thì các bản ghi
--    tham chiếu được "bỏ phân loại" thay vì bị chặn hay bị xóa theo.

-- ---------- fin_products ----------
ALTER TABLE fin_products
    ADD COLUMN user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE;

-- Backfill chủ từ category cho dữ liệu đã có (V23 đã gắn user_id cho category).
UPDATE fin_products p
SET user_id = c.user_id
FROM fin_categories c
WHERE c.id = p.fin_category_id;

ALTER TABLE fin_products ALTER COLUMN fin_category_id DROP NOT NULL;

ALTER TABLE fin_products DROP CONSTRAINT IF EXISTS fin_products_fin_category_id_fkey;
ALTER TABLE fin_products
    ADD CONSTRAINT fin_products_fin_category_id_fkey
    FOREIGN KEY (fin_category_id) REFERENCES fin_categories(id) ON DELETE SET NULL;

-- Tên product duy nhất theo (user, category). Unique constraint thường coi NULL là khác nhau,
-- nên dùng unique index với COALESCE để product không có category cũng không trùng tên.
ALTER TABLE fin_products DROP CONSTRAINT IF EXISTS uk_fin_products_category_name;
CREATE UNIQUE INDEX uk_fin_products_user_category_name
    ON fin_products (user_id, COALESCE(fin_category_id, ''), name);

-- ---------- fin_transactions ----------
ALTER TABLE fin_transactions ALTER COLUMN fin_product_id DROP NOT NULL;

ALTER TABLE fin_transactions DROP CONSTRAINT IF EXISTS fin_transactions_fin_product_id_fkey;
ALTER TABLE fin_transactions
    ADD CONSTRAINT fin_transactions_fin_product_id_fkey
    FOREIGN KEY (fin_product_id) REFERENCES fin_products(id) ON DELETE SET NULL;
