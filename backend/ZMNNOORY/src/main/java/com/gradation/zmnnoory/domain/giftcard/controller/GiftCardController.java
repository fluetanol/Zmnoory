package com.gradation.zmnnoory.domain.giftcard.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.giftcard.dto.request.GiftCardCreateRequest;
import com.gradation.zmnnoory.domain.giftcard.dto.response.GiftCardResponse;
import com.gradation.zmnnoory.domain.giftcard.dto.response.MyGiftCardResponse;
import com.gradation.zmnnoory.domain.giftcard.service.GiftCardService;
import com.gradation.zmnnoory.domain.member.annotation.AdminOnly;
import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
import com.gradation.zmnnoory.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@Tag(name = "기프티콘 API", description = "기프티콘 관리 및 조회 API")
public class GiftCardController {

    private final GiftCardService giftCardService;

    @Operation(
            summary = "기프티콘 등록",
            description = """
                    관리자가 새로운 기프티콘을 시스템에 등록합니다.
                    - 상품 ID와 기프티콘 정보를 입력합니다.
                    - 중복된 기프티콘 번호는 등록할 수 없습니다.
                    """
    )
    @PostMapping("/api/admin/giftcards")
    @AdminOnly
    public BaseResponse<GiftCardResponse> createGiftCard(
            @Valid @RequestBody GiftCardCreateRequest request) {

        GiftCardResponse response = giftCardService.createGiftCard(request);

        return BaseResponse.<GiftCardResponse>builder()
                .status(HttpStatus.CREATED)
                .data(response)
                .build();
    }

    @Operation(
            summary = "모든 기프티콘 조회",
            description = """
                    관리자가 시스템에 등록된 모든 기프티콘을 조회합니다.
                    """
    )
    @GetMapping("/api/admin/giftcards")
    @AdminOnly
    public BaseResponse<List<GiftCardResponse>> getAllGiftCards() {

        List<GiftCardResponse> response = giftCardService.getAllGiftCards();

        return BaseResponse.<List<GiftCardResponse>>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "기프티콘 상세 조회",
            description = """
                    특정 기프티콘의 상세 정보를 조회합니다.
                    """
    )
    @GetMapping("/api/admin/giftcards/{id}")
    @AdminOnly
    public BaseResponse<GiftCardResponse> getGiftCard(@PathVariable Long id) {

        GiftCardResponse response = giftCardService.getGiftCard(id);

        return BaseResponse.<GiftCardResponse>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    @Operation(
            summary = "내 기프티콘 목록",
            description = """
                    로그인한 사용자가 구매한 기프티콘 목록을 조회합니다.
                    - 구매한 순서대로 정렬됩니다.
                    - 사용 상태와 만료 여부를 확인할 수 있습니다.
                    """
    )
    @GetMapping("/api/giftcards/my")
    public BaseResponse<List<MyGiftCardResponse>> getMyGiftCards(
            @LoginMember Member member) {

        List<MyGiftCardResponse> response = giftCardService.getMyGiftCards(member);

        return BaseResponse.<List<MyGiftCardResponse>>builder()
                .status(HttpStatus.OK)
                .data(response)
                .build();
    }

    

    @Operation(
            summary = "기프티콘 삭제",
            description = """
                    관리자가 기프티콘을 시스템에서 삭제합니다.
                    - 이미 할당된 기프티콘은 삭제할 수 없습니다.
                    """
    )
    @DeleteMapping("/api/admin/giftcards/{id}")
    @AdminOnly
    public BaseResponse<Void> deleteGiftCard(@PathVariable Long id) {

        giftCardService.deleteGiftCard(id);

        return BaseResponse.<Void>builder()
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}