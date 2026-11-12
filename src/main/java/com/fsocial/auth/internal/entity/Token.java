package com.fsocial.auth.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;
import com.fsocial.user.internal.entity.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
@Table(name = "token")
public class Token {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(length = 36)
    String id;

    @Column(name = "token", columnDefinition = "text")
    String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    User user;
}
