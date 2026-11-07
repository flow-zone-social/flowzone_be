package com.fsocial.user.internal.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import com.fsocial.shared.persistence.AbstractEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "follow", uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followee_id"}))
public class Follow extends  AbstractEntity<String>{

    @Column(name = "follower_id", length = 36)
    String followerId;

    @Column(name = "followee_id", length = 36)
    String followeeId;

}
