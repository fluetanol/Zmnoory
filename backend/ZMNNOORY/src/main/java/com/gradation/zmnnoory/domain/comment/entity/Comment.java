package com.gradation.zmnnoory.domain.comment.entity;

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
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  (fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 500, nullable = false)
    private String content;

    @Builder
    public Comment(Video video, Member member, String content) {
        this.video = video;
        this.member = member;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
