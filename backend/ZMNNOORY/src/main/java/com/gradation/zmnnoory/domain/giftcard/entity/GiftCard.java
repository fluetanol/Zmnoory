package com.gradation.zmnnoory.domain.giftcard.entity;

import com.gradation.zmnnoory.common.entity.BaseEntity;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GiftCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String giftCardImage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    private GiftCard(String giftCardImage,
                     Product product,
                     Member member) {
        this.giftCardImage = giftCardImage;
        this.product = product;
        this.member = member;
    }

    public void assignToMember(Member member) {
        this.member = member;
    }
}