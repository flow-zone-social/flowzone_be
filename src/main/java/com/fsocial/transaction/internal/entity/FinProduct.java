package com.fsocial.transaction.internal.entity;

import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "fin_products")
public class FinProduct extends AbstractEntity<String> {
    @Column(name = "name")
    String name;
    @Column(name = "descriptions")
    String descriptions;
    @Column(name = "avatar_url")
    String avatarUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fin_category_id")
    FinCategory finCategory;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    User user;
}
