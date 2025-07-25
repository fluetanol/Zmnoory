package com.gradation.zmnnoory.domain.member.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
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

    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String nickname;

    private LocalDate birthday;

    @Builder
    private Member(String email,
                   String password,
                   Gender gender,
                   String nickname,
                   LocalDate birthday
    ) {
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.nickname = nickname;
        this.birthday = birthday;
    }

    public void update(MemberUpdateRequest memberUpdateRequest) {
        this.email = memberUpdateRequest.email();
        this.password = memberUpdateRequest.password();
        this.gender = memberUpdateRequest.gender();
        this.nickname = memberUpdateRequest.nickname();
        this.birthday = memberUpdateRequest.birthday();
    }
}
