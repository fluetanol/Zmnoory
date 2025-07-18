package com.gradation.zmnnoory.domain.member.entity;

import com.gradation.zmnnoory.domain.member.dto.SignUpRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "members") // db 예약어 member 충돌 방지
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String password;
    private String gender;

    @Builder
    private Member(String email, String password, String gender) {
        this.email = email;
        this.password = password;
        this.gender = gender;
    }
}
