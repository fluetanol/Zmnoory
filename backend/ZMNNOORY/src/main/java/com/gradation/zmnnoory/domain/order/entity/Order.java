    package com.gradation.zmnnoory.domain.order.entity;


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
    @Table(name = "orders")
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class Order extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "member_id", nullable = false)
        private Member member;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "product_id", nullable = false)
        private Product product;

        @Builder
        public Order(Member member, Product product) {
            this.member = member;
            this.product = product;
        }

    }
