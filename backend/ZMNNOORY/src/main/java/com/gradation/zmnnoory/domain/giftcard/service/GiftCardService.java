package com.gradation.zmnnoory.domain.giftcard.service;

import com.gradation.zmnnoory.domain.giftcard.dto.request.GiftCardCreateRequest;
import com.gradation.zmnnoory.domain.giftcard.dto.response.GiftCardResponse;
import com.gradation.zmnnoory.domain.giftcard.dto.response.MyGiftCardResponse;
import com.gradation.zmnnoory.domain.giftcard.entity.GiftCard;
import com.gradation.zmnnoory.domain.giftcard.exception.GiftCardNotFoundException;
import com.gradation.zmnnoory.domain.giftcard.repository.GiftCardRepository;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.order.entity.Order;
import com.gradation.zmnnoory.domain.order.repository.OrderRepository;
import com.gradation.zmnnoory.domain.product.entity.Product;
import com.gradation.zmnnoory.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public GiftCardResponse createGiftCard(GiftCardCreateRequest request) {
        // 상품 조회
        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다."));

        // 기프티콘 생성
        GiftCard giftCard = GiftCard.builder()
                .giftCardImage(request.giftCardImage())
                .product(product)
                .build();

        GiftCard savedGiftCard = giftCardRepository.save(giftCard);
        return GiftCardResponse.from(savedGiftCard);
    }

    public List<GiftCardResponse> getAllGiftCards() {
        List<GiftCard> giftCards = giftCardRepository.findAll();
        return giftCards.stream()
                .map(GiftCardResponse::from)
                .toList();
    }

    public GiftCardResponse getGiftCard(Long id) {
        GiftCard giftCard = giftCardRepository.findById(id)
                .orElseThrow(GiftCardNotFoundException::new);
        return GiftCardResponse.from(giftCard);
    }

    public List<MyGiftCardResponse> getMyGiftCards(Member member) {
        List<GiftCard> giftCards = giftCardRepository.findByMember(member);
        return giftCards.stream()
                .map(MyGiftCardResponse::from)
                .toList();
    }

    @Transactional
    public GiftCard assignGiftCardToOrder(Product product, Member member) {
        GiftCard giftCard = giftCardRepository
                .findFirstByProductAndMemberIsNullOrderByCreatedAtAsc(product)
                .orElseThrow(() -> new RuntimeException("사용 가능한 기프티카드가 없습니다.")); // Custom exception needed

        giftCard.assignToMember(member); // Assuming assignToMember method exists in GiftCard entity
        return giftCard;
    }

    

    @Transactional
    public void deleteGiftCard(Long id) {
        GiftCard giftCard = giftCardRepository.findById(id)
                .orElseThrow(GiftCardNotFoundException::new);
        giftCardRepository.delete(giftCard);
    }

    public long getAvailableGiftCardCount(Product product) {
        return giftCardRepository.countByProductAndMemberIsNull(product);
    }
}