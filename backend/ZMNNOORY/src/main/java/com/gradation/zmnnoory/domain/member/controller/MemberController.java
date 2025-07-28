package com.gradation.zmnnoory.domain.member.controller;

import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
import com.gradation.zmnnoory.domain.member.dto.MemberUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.PasswordUpdateRequest;
import com.gradation.zmnnoory.domain.member.dto.request.SignUpRequest;
import com.gradation.zmnnoory.domain.member.dto.response.MemberResponse;
import com.gradation.zmnnoory.domain.member.entity.Member;
import com.gradation.zmnnoory.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 모든 응답은 common.handler.ApiResponseHandler 에서 후처리됨
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "사용자 API", description = "사용자 관련 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/sign-up")
    @Operation(
            summary = "회원가입",
            description = """
                    새로운 사용자를 등록합니다.
                    - 요청 본문에 이메일, 비밀번호, 닉네임 등이 포함됩니다.
                    - 중복된 이메일일 경우 409 Conflict 에러가 발생할 수 있습니다.
                    """
    )
    public BaseResponse<MemberResponse> signUp(
            @RequestBody @Validated SignUpRequest signUpRequest
    ) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.CREATED)
                .data(memberService.createMember(signUpRequest))
                .build();
    }

    @Operation(
            summary = "단일 사용자 조회",
            description = """
                    특정 ID를 가진 사용자의 정보를 조회합니다.
                    - `memberId` 경로 파라미터를 기준으로 조회합니다.
                    - 존재하지 않을 경우 404 Not Found 오류가 발생할 수 있습니다.
                    """
    )
    @GetMapping("/{memberId}")
    public BaseResponse<MemberResponse> getMember(
            @PathVariable("memberId") Long memberId
    ) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .data(memberService.findById(memberId))
                .build();
    }

    @Operation(
            summary = "전체 사용자 목록 조회",
            description = """
                    시스템에 등록된 모든 사용자의 정보를 리스트 형태로 반환합니다.
                    - 페이지네이션이나 정렬 없이 전체 데이터를 반환합니다.
                    - 관리자 권한이 있어야만 가능합니다.
                    - 권한은 운영팀에 문의하세요.
                    """
    )
    @GetMapping("/admin")
    public BaseResponse<List<MemberResponse>> getMembers() {
        return BaseResponse.<List<MemberResponse>>builder()
                .status(HttpStatus.OK)
                .data(memberService.findAll())
                .build();
    }

    @Operation(
            summary = "사용자 정보 수정",
            description = """
                    사용자의 닉네임, 생년월일 등의 정보를 수정합니다.
                    - 이메일을 기준으로 사용자를 식별합니다.
                    - 요청 본문에 수정할 닉네임, 생년월일 정보가 포함되어야 합니다.
                    - 존재하지 않는 이메일일 경우 404 오류가 발생할 수 있습니다.
                    """
    )
    @PutMapping("/{email}")
    public BaseResponse<MemberResponse> updateUserInfo(
            @PathVariable String email,
            @Validated MemberUpdateRequest memberUpdateRequest
    ) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .message("업데이트 성공")
                .data(memberService.updateMemberInfoWith(email, memberUpdateRequest))
                .build();
    }

    @Operation(
            summary = "사용자 비밀번호 수정",
            description = """
                    사용자의 비밀번호를 수정합니다.
                    - 이메일을 기준으로 사용자를 식별합니다.
                    - 요청 본문에 수정할 닉네임, 생년월일 정보가 포함되어야 합니다.
                    - 존재하지 않는 이메일일 경우 404 오류가 발생할 수 있습니다.
                    """
    )
    @PatchMapping("/password")
    public BaseResponse<MemberResponse> updateUserPassword(
            @LoginMember Member member,
            @Validated PasswordUpdateRequest passwordUpdateRequest
    ) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .data(memberService.updateMemberPassword(member, passwordUpdateRequest))
                .build();
    }

    @Operation(
            summary = "사용자 권한 수정",
            description = """
                    사용자의 권한을 변경합니다.
                    - 관리자 유저만 실행할 수 있습니다.
                    - 권한은 운영팀에 문의하세요
                    """
    )
    @PatchMapping("/admin/role-change/{targetId}")
    public BaseResponse<MemberResponse> updateMemberRole(@PathVariable Long targetId) {
        return BaseResponse.<MemberResponse>builder()
                .status(HttpStatus.OK)
                .message(String.format("%d 유저의 권한을 변경하였습니다.", targetId))
                .data(memberService.updateMemberRole(targetId))
                .build();
    }
}
