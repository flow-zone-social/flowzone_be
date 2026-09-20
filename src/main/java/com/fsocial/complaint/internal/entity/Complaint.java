package com.fsocial.complaint.internal.entity;

import com.fsocial.complaint.internal.enums.ComplaintType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import com.fsocial.shared.persistence.AbstractEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "complaint")
@SuperBuilder
public class Complaint extends AbstractEntity<String> {

    @Column(name = "target_id", length = 36, nullable = false)
    String targetId;

    @Enumerated(EnumType.STRING)
    @Column(name = "complaint_type", length = 16, nullable = false)
    ComplaintType complaintType;

    @Column(name = "is_read", nullable = false)
    boolean isRead;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ComplaintDetail> details = new ArrayList<>();

    public void addDetail(ComplaintDetail detail) {
        detail.setComplaint(this);
        details.add(detail);
    }
}
