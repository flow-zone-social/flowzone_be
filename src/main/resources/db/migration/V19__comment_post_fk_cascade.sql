-- comment.post_id trước đây không có FK (cleanup dự định chạy async qua RabbitMQ,
-- nhưng nhánh đó chưa từng được publish nên comment bị mồ côi khi xóa post).
-- Thêm FK thật + ON DELETE CASCADE, theo đúng pattern của attachments/post_like/seen_post/post_tag.
ALTER TABLE comment
    ADD CONSTRAINT fk_comment_post_id FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE;
