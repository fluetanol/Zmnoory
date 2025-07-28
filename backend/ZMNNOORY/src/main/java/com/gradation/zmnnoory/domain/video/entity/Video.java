package com.gradation.zmnnoory.domain.video.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.participation.entity.Participation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participation_id", nullable = false, unique = true)
    private Participation participation;

    @Column(length = 30)
    private String title;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(length = 255)
    private String videoUrl;

    @Column(length = 255)
    private String thumbnailUrl;

    @Builder
    public Video(Participation participation, String title, String description, 
                 Boolean isPublic, String videoUrl, String thumbnailUrl) {
        this.participation = participation;
        this.title = title;
        this.description = description;
        this.isPublic = isPublic != null ? isPublic : false;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public void updateVideoInfo(String title, String description, Boolean isPublic, 
                               String videoUrl, String thumbnailUrl) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic != null ? isPublic : this.isPublic;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isVideoUploaded() {
        return this.videoUrl != null && !this.videoUrl.trim().isEmpty();
    }
}