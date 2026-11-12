-- Category thuộc về từng user (trước đây là danh mục dùng chung).
-- Product thuộc category nên thuộc cùng chủ, không cần thêm cột user_id cho fin_products.
--
-- user_id để NULL được: nếu V22 đã có dữ liệu cũ thì không bị lỗi migrate; các dòng cũ
-- (user_id NULL) sẽ không hiện với user nào. Entity/service luôn gán user_id khi tạo mới.

ALTER TABLE fin_categories
    ADD COLUMN user_id VARCHAR(36) REFERENCES users(id) ON DELETE CASCADE;

-- Tên category duy nhất theo từng user thay vì toàn hệ thống.
ALTER TABLE fin_categories DROP CONSTRAINT uk_fin_categories_name;
ALTER TABLE fin_categories ADD CONSTRAINT uk_fin_categories_user_name UNIQUE (user_id, name);

-- Xóa user -> xóa category -> xóa product của user đó (không bị FK chặn).
-- Việc "không cho xóa category còn product" vẫn do service kiểm tra (trả 409).
ALTER TABLE fin_products DROP CONSTRAINT IF EXISTS fin_products_fin_category_id_fkey;
ALTER TABLE fin_products
    ADD CONSTRAINT fin_products_fin_category_id_fkey
    FOREIGN KEY (fin_category_id) REFERENCES fin_categories(id) ON DELETE CASCADE;
