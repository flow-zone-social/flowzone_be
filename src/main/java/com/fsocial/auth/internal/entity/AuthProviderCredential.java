package com.fsocial.auth.internal.entity;

import com.fsocial.auth.api.AuthProvider;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.entity.User;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@Table(name = "auth_provider")
public class AuthProviderCredential extends AbstractEntity<String> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", length = 16, nullable = false)
    AuthProvider provider;

    @Column(name = "password")
    String password;

    @Column(name = "provider_user_id", length = 255)
    String providerUserId;
}
