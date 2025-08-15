package com.gradation.zmnnoory.domain.datarequest.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "data_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactInfo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String dataRequirements;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DataRequestStatus status;

    private LocalDateTime requestDate;

    private LocalDateTime processedDate;

    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @Builder
    private DataRequest(String companyName, String contactInfo, String dataRequirements) {
        this.companyName = companyName;
        this.contactInfo = contactInfo;
        this.dataRequirements = dataRequirements;
        this.status = DataRequestStatus.PENDING;
        this.requestDate = LocalDateTime.now();
    }

    public void updateStatus(DataRequestStatus status) {
        this.status = status;
        this.processedDate = LocalDateTime.now();
    }

    public void updateAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}