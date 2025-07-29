package com.gradation.zmnnoory.domain.order.service;

import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.order.dto.response.OrderResponse;
import com.gradation.zmnnoory.domain.order.entity.Order;
import com.gradation.zmnnoory.domain.order.exception.InsufficientPointException;
import com.gradation.zmnnoory.domain.order.exception.ProductNotFoundException;
import com.gradation.zmnnoory.domain.order.repository.OrderRepository;
import com.gradation.zmnnoory.domain.product.entity.Product;
import com.gradation.zmnnoory.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderResponse purchaseProduct(Member member, String productTitle) {
        // 1. 상품 조회
        Product product = productRepository.findByTitle(productTitle)
                .orElseThrow(ProductNotFoundException::new);

        // 2. 포인트 부족 검사 및 차감
        if (member.getPoint() < product.getPrice()) {
            throw new InsufficientPointException();
        }
        member.usePoint(product.getPrice());

        // 3. 주문 생성 및 저장
        Order order = Order.builder()
                .member(member)
                .product(product)
                .build();
        
        orderRepository.save(order);

        // 4. 응답 반환
        return OrderResponse.of(product);
    }
}
