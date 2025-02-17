package com.potato.balbambalbam.user.withdrawal.controller;

import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import com.potato.balbambalbam.user.withdrawal.dto.WithdrawalResponseDto;
import com.potato.balbambalbam.user.withdrawal.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@Tag(name = "Withdrawal API", description = "탈퇴 사유를 저장한다.")
public class WithdrawalController {
    private final JWTUtil jwtUtil;
    //private final WithdrawalService withdrawalService;

    @Operation(summary = "탈퇴 사유 받기", description = "사용자의 월별 출석 날짜를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = WithdrawalResponseDto.class))),
    })
    @PostMapping("/user/withdrawal")
    public ResponseEntity<WithdrawalResponseDto> setWithdrawal(@RequestHeader("access") String access) {
        Long userId = jwtUtil.getUserId(access);
        //return ResponseEntity.ok();
        return null;
    }
}
