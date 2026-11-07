-- Entity Attachments.owner và FaqView.user đã chuyển từ String sang @ManyToOne Account,
-- nhưng V1/V13 chỉ khai báo owner_id / user_id là VARCHAR(36) thuần, không có FK.
-- Cả hai cột đều nullable nên dọn giá trị mồ côi về NULL trước khi thêm FK
-- (nếu không ADD CONSTRAINT sẽ fail khi còn id không tồn tại trong account).
-- ON DELETE SET NULL: xóa account không kéo theo xóa file (attachment còn có thể
-- được faq.attachment_id tham chiếu) và không làm mất lượt xem FAQ.

UPDATE attachments a SET owner_id = NULL
WHERE owner_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM account ac WHERE ac.id = a.owner_id);

UPDATE faq_view v SET user_id = NULL
WHERE user_id IS NOT NULL
  AND NOT EXISTS (SELECT 1 FROM account ac WHERE ac.id = v.user_id);

ALTER TABLE attachments
    ADD CONSTRAINT fk_attachments_owner FOREIGN KEY (owner_id) REFERENCES account(id) ON DELETE SET NULL;

ALTER TABLE faq_view
    ADD CONSTRAINT fk_faq_view_user FOREIGN KEY (user_id) REFERENCES account(id) ON DELETE SET NULL;

CREATE INDEX idx_faq_view_user ON faq_view(user_id);
