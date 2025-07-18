package com.gradation.zmnnoory.domain.stage.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "stage")
@Getter  // 모든 필드의 Getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA가 stage를 사용할 수 있게
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 아래는 내가 정한 DB 테이블의 필드들
    private String title;
    private String description;
    private String difficulty;
    private Integer rewardTotal;

    @Builder
    public Stage(String title, String description, String difficulty, Integer rewardTotal) {
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.rewardTotal = rewardTotal;
    }
}
