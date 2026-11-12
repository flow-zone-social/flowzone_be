package com.fsocial.user.internal.entity;

import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.transaction.internal.entity.FinTransaction;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

import com.fsocial.shared.persistence.AbstractEntity;
import com.fsocial.user.internal.rbac.Role;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends AbstractEntity<String> {

    @Column(name = "username", length = 64)
    String username;

    @Column(name = "first_name", length = 128)
    String firstName;

    @Column(name = "last_name", length = 128)
    String lastName;

    @Column(name = "display_name")
    String displayName;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "gender", nullable = false)
    int gender;

    @Column(name = "avatar", columnDefinition = "text")
    String avatar;

    @Column(name = "background", columnDefinition = "text")
    String background;

    @Column(name = "bio", columnDefinition = "text")
    String bio;

    @Column(name = "address", columnDefinition = "text")
    String address;

    @Column(name = "is_kol", nullable = false)
    @Builder.Default
    boolean isKOL = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    Role role;

    @Column(name = "email")
    String email;

    @Column(name = "status", nullable = false)
    @Builder.Default
    boolean status = true;

    // follower/following (Set<String>) -> bảng `follow`, truy vấn qua FollowRepository

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    Boolean isPublic = true;

    @OneToMany(mappedBy = "owner")
    List<Attachments> attachments;

    @OneToMany(mappedBy = "user")
    List<FinTransaction> transactions;
}
