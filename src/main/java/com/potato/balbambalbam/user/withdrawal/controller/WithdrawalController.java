package com.potato.balbambalbam.user.withdrawal.controller;

import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import com.potato.balbambalbam.user.withdrawal.dto.WithdrawalRequestDto;
import com.potato.balbambalbam.user.withdrawal.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@Tag(name = "Withdrawal API", description = "탈퇴 사유를 저장한다.")
public class WithdrawalController {

    private final JWTUtil jwtUtil;
    private final WithdrawalService withdrawalService;

    @Operation(summary = "사용자 계정 탈퇴 사유", description = "사용자의 탈퇴 사유를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "탈퇴 사유 저장 성공"),
    })
    @PostMapping("/users/withdrawal")
    public ResponseEntity<?> setWithdrawal(
            @RequestHeader("access") String access, @RequestBody WithdrawalRequestDto requestDto) {

        Long userId = jwtUtil.getUserId(access);
        withdrawalService.processWithdrawal(userId, requestDto);

        return ResponseEntity.ok().body("사용자의 탈퇴 사유 저장 완료");
    }
}
