package com.gradation.zmnnoory.domain.product.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.annotation.AdminOnly;
import com.gradation.zmnnoory.domain.product.dto.request.ProductCreateRequest;
import com.gradation.zmnnoory.domain.product.dto.request.ProductUpdateRequest;
import com.gradation.zmnnoory.domain.product.dto.response.ProductResponse;
import com.gradation.zmnnoory.domain.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품 API", description = "상품 등록, 조회, 수정, 삭제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "상품 등록",
            description =
            """
            새로운 상품 정보를 등록합니다.
            - 제목, 카테고리, 가격, 썸네일 URL 등을 입력받습니다.
            - 입력값은 유효성 검증을 거치며, 유효하지 않을 경우 400 에러를 반환합니다.
            - 관리자 권한이 필요합니다.
            """
    )
    @AdminOnly
    @PostMapping
    public BaseResponse<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {

        return BaseResponse.<ProductResponse>builder()
                .status(HttpStatus.CREATED)
                .data(productService.createProduct(request))
                .build();
    }

    @Operation(
            summary = "전체 상품 목록 조회",
            description =
            """
            등록된 모든 상품 정보를 리스트 형태로 반환합니다.
            - 정렬 또는 필터링 없이 전체 상품 데이터를 반환합니다.
            - 각 상품은 요약 정보(DTO)로 제공됩니다.
            """
    )
    @GetMapping
    public BaseResponse<List<ProductResponse>> getAllProducts() {

        return BaseResponse.<List<ProductResponse>>builder()
                .status(HttpStatus.OK)
                .data(productService.getAllProducts())
                .build();
    }

    @Operation(
            summary = "상품 단건 조회",
            description =
            """
            특정 ID에 해당하는 상품 정보를 상세히 조회합니다.
            - 존재하지 않는 ID를 조회하면 404 에러가 반환됩니다.
            """
    )
    @GetMapping("/{id}")
    public BaseResponse<ProductResponse> getProductById(
            @PathVariable Long id) {

        return BaseResponse.<ProductResponse>builder()
                .status(HttpStatus.OK)
                .data(productService.getProductById(id))
                .build();
    }

    @Operation(
            summary = "상품 수정",
            description =
            """
            기존 상품 정보를 수정합니다.
            - 상품 ID를 경로 파라미터로 받아 해당 상품을 수정합니다.
            - 요청 본문으로 수정할 상품 정보를 전달하며, 유효성 검사가 수행됩니다.
            - 존재하지 않는 상품의 경우 404 에러가 반환됩니다.
            - 관리자 권한이 필요합니다.
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public BaseResponse<ProductResponse> updateProduct(
            @PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {

        return BaseResponse.<ProductResponse>builder()
                .status(HttpStatus.OK)
                .data(productService.updateProduct(id, request))
                .build();
    }

    @Operation(
            summary = "상품 삭제",
            description =
            """
            특정 ID에 해당하는 상품 정보를 삭제합니다.
            - 삭제 후 204 No Content 상태 코드가 반환됩니다.
            - 존재하지 않는 상품을 삭제하려 할 경우 404 에러가 발생합니다.
            - 관리자 권한이 필요합니다.
            """
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteProduct(
            @PathVariable Long id) {
        productService.deleteProduct(id);

        return BaseResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}