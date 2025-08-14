package com.gradation.zmnnoory.domain.giftcard.repository;

import com.gradation.zmnnoory.domain.giftcard.entity.GiftCard;
import com.gradation.zmnnoory.domain.giftcard.entity.GiftCardStatus;
import com.gradation.zmnnoory.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

import com.gradation.zmnnoory.domain.member.entity.Member; 

public interface GiftCardRepository extends JpaRepository<GiftCard, Long> {

    // 특정 상품의 멤버가 할당되지 않은 기프티콘 중 첫 번째 조회
    Optional<GiftCard> findFirstByProductAndMemberIsNullOrderByCreatedAtAsc(Product product);

    // 특정 상품의 멤버가 할당되지 않은 기프티콘 개수 조회
    long countByProductAndMemberIsNull(Product product);

    // 특정 멤버에게 할당된 기프티콘 조회
    List<GiftCard> findByMember(Member member);

    // 특정 상품의 모든 기프티콘 조회 (keeping this as it might be useful for admin)
    List<GiftCard> findByProductOrderByCreatedAtDesc(Product product);
}