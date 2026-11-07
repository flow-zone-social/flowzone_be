package com.fsocial.faq.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Đánh giá của 1 user cho 1 FAQ (mỗi user chỉ 1 đánh giá / FAQ, đánh giá lại sẽ ghi đè).
 * Điểm trung bình/số lượt đánh giá tính bằng aggregate query trên bảng này để đảm bảo chính xác.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "faq_rating")
@IdClass(FaqRating.Key.class)
public class FaqRating {

    @Id
    @Column(name = "faq_id", length = 36)
    String faqId;

    @Id
    @Column(name = "user_id", length = 36)
    String userId;

    @Column(name = "score", nullable = false)
    Integer score;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Key implements Serializable {
        private String faqId;
        private String userId;
    }
}
