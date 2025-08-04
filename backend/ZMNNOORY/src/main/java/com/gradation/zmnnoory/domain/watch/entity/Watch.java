package com.gradation.zmnnoory.domain.watch.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.video.entity.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "watches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "video_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Watch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Builder
    public Watch(Member member, Video video) {
        this.member = member;
        this.video = video;
    }

}
