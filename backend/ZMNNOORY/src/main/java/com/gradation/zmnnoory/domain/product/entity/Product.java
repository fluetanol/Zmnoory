package com.gradation.zmnnoory.domain.product.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Category category;

    @Column(nullable = false)
    private Long price;

    @Column(nullable = false)
    private String thumbnail;

    @Builder
    private Product(String title,
                   Category category,
                   Long price,
                   String thumbnail) {
        this.title = title;
        this.category = category;
        this.price = price;
        this.thumbnail = thumbnail;
    }
}
