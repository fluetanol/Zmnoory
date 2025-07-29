package com.gradation.zmnnoory.domain.order.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.order.dto.request.OrderRequest;
import com.gradation.zmnnoory.domain.order.dto.response.OrderResponse;
import com.gradation.zmnnoory.domain.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Validated
@Tag(name = "주문 API", description = "기프티콘 상품 구매 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "기프티콘 구매",
            description =
                    """
                    포인트를 사용하여 기프티콘 상품을 구매합니다.
                    - 상품 ID를 요청 본문으로 전달합니다.
                    - 보유 포인트가 부족할 경우 예외가 발생합니다.
                    - 구매에 성공하면 해당 상품 정보가 응답됩니다.
                    """
    )
    @PostMapping
    public BaseResponse<OrderResponse> purchaseProduct(
            @LoginMember Member member,
            @Valid @RequestBody OrderRequest request) {

        OrderResponse response = orderService.purchaseProduct(member, request.productTitle());

        return BaseResponse.<OrderResponse>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }
}
