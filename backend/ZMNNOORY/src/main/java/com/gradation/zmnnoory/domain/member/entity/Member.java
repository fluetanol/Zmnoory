package com.gradation.zmnnoory.domain.member.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.exception.IllegalPointUsageException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "members") // db 예약어 member 충돌 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false)
    private LocalDate birthday;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recommender_id")
    private Member recommender;

    @Column(nullable = false)
    private Long point = 0L;

    @Builder
    private Member(String email,
                   String password,
                   Gender gender,
                   String nickname,
                   LocalDate birthday,
                   Member recommender
    ) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.nickname = nickname;
        this.birthday = birthday;
        this.recommender = recommender;
    }

    public void update(MemberUpdateRequest memberUpdateRequest) {
        this.email = memberUpdateRequest.email();
        this.gender = memberUpdateRequest.gender();
        this.nickname = memberUpdateRequest.nickname();
        this.birthday = memberUpdateRequest.birthday();
    }

    public void addPoint(long point) {
        this.point += point;
    }

    public void usePoint(long point) {
        if (this.point < point) {
            throw new IllegalPointUsageException();
        }

        this.point -= point;
    }
}
