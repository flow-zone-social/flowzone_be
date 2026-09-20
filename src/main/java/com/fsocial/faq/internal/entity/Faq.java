package com.fsocial.faq.internal.entity;

import com.fsocial.faq.internal.enums.FaqType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import com.fsocial.media.internal.entity.Attachments;
import com.fsocial.shared.persistence.AbstractEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "faq")
@SuperBuilder
public class Faq extends AbstractEntity<String> {

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "description", columnDefinition = "text")
    String description;

    @Column(name = "content", nullable = false, columnDefinition = "text")
    String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 16, nullable = false)
    FaqType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attachment_id")
    Attachments attachment;
}
