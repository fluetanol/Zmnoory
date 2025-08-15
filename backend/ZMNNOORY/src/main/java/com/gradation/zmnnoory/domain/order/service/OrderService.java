package com.gradation.zmnnoory.domain.order.service;

import com.gradation.zmnnoory.domain.giftcard.entity.GiftCard;
import com.gradation.zmnnoory.domain.giftcard.service.GiftCardService;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.repository.MemberRepository;
import com.gradation.zmnnoory.domain.order.dto.response.OrderResponse;
import com.gradation.zmnnoory.domain.order.entity.Order;
import com.gradation.zmnnoory.domain.order.exception.InsufficientPointException;
import com.gradation.zmnnoory.domain.order.exception.ProductNotFoundException;
import com.gradation.zmnnoory.domain.order.repository.OrderRepository;
import com.gradation.zmnnoory.domain.product.entity.Product;
import com.gradation.zmnnoory.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final GiftCardService giftCardService;
    private final MemberRepository memberRepository;

    @Transactional
    public OrderResponse purchaseProduct(Member member, String productTitle) {
        // 1. 상품 조회
        Product product = productRepository.findByTitle(productTitle)
                .orElseThrow(ProductNotFoundException::new);

        // 2. Member를 영속성 컨텍스트에서 다시 조회
        Member managedMember = memberRepository.findById(member.getId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        log.info("구매 전 포인트: {} (Member ID: {})", managedMember.getPoint(), managedMember.getId());
        log.info("상품 가격: {} (Product: {})", product.getPrice(), product.getTitle());

        // 3. 포인트 부족 검사 및 차감
        if (managedMember.getPoint() < product.getPrice()) {
            throw new InsufficientPointException();
        }
        managedMember.usePoint(product.getPrice());
        
        log.info("구매 후 포인트: {} (Member ID: {})", managedMember.getPoint(), managedMember.getId());

        // 4. 사용 가능한 기프티콘 할당
        try {
            giftCardService.assignGiftCardToOrder(product, managedMember);
            log.info("기프티콘 할당 성공");
        } catch (Exception e) {
            log.error("기프티콘 할당 실패: {}", e.getMessage());
            throw e;
        }

        // 5. 주문 생성 및 저장
        Order order = Order.builder()
                .member(managedMember)
                .product(product)
                .build();
        
        Order savedOrder = orderRepository.save(order);

        // 5. 응답 반환
        return OrderResponse.of(product);
    }
}
