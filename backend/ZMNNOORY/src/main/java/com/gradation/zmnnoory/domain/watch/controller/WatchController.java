    package com.gradation.zmnnoory.domain.watch.controller;

    import com.gradation.zmnnoory.common.dto.BaseResponse;
    import com.gradation.zmnnoory.domain.member.annotation.LoginMember;
    import com.gradation.zmnnoory.domain.member.entity.Member;
    import com.gradation.zmnnoory.domain.watch.dto.response.WatchResponse;
    import com.gradation.zmnnoory.domain.watch.service.WatchService;
    import io.swagger.v3.oas.annotations.Operation;
    import io.swagger.v3.oas.annotations.tags.Tag;
    import lombok.RequiredArgsConstructor;
    import org.springframework.http.HttpStatus;
    import org.springframework.validation.annotation.Validated;
    import org.springframework.web.bind.annotation.*;

    @Tag(name = "시청 기록 API", description = "비디오 시청 기록 관리")
    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api/watches")
    @Validated
    public class WatchController {

        private final WatchService watchService;

        // 1. 비디오 시청 완료 기록
        @Operation(
                summary = "비디오 시청 완료 기록",
                description = """
                비디오 시청 완료를 기록합니다.
                - 이미 시청한 비디오의 경우 중복 기록되지 않습니다.
                - 시청 완료 후 현재 시청 상태와 총 시청 수를 반환합니다.
                """
        )
        @PostMapping("/videos/{videoId}")
        public BaseResponse<WatchResponse> recordWatch(
                @PathVariable Long videoId,
                @LoginMember Member member) {

            watchService.recordWatch(member.getId(), videoId);

            return BaseResponse.<WatchResponse>builder()
                    .status(HttpStatus.OK)
                    .data(watchService.getWatchStatus(member.getId(), videoId))
                    .build();
        }

        // 2. 시청 상태 조회
        @Operation(
                summary = "시청 상태 조회",
                description = """
                특정 사용자의 비디오 시청 상태를 조회합니다.
                - 사용자가 해당 비디오를 시청했는지 여부와 총 시청 수를 반환합니다.
                """
        )
        @GetMapping("/videos/{videoId}")
        public BaseResponse<WatchResponse> getWatchStatus(
                @PathVariable Long videoId,
                @LoginMember Member member) {

            return BaseResponse.<WatchResponse>builder()
                    .status(HttpStatus.OK)
                    .data(watchService.getWatchStatus(member.getId(), videoId))
                    .build();
        }

    }
