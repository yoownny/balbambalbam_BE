package com.potato.balbambalbam.myReport.report.controller;

import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.myReport.report.dto.CardLevelRequestDto;
import com.potato.balbambalbam.myReport.report.dto.ReportInfoDto;
import com.potato.balbambalbam.myReport.report.service.ReportInfoService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "My Report API", description = "My Report에 필요한 ...을 반환한다.")
public class ReportController {
    private final JWTUtil jwtUtil;
    private final ReportInfoService reportInfoService;

    @Operation(summary = "My report 화면 정보 조회", description = "My report 화면에 필요한 모든 정보를 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "My report 화면 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportInfoDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/report")
    public ResponseEntity<ReportInfoDto> getHomeInfo(@RequestHeader("access") String access) {
        Long userId = jwtUtil.getUserId(access);
        ReportInfoDto response = reportInfoService.getMyReportInfo(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 카드 레벨 설정", description = "사용자가 선택한 카드 레벨을 설정한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카드 레벨 설정 성공", content = @Content),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content)
    })
    @PostMapping("/report/cardLevel")
    public ResponseEntity<String> setCardLevel(@RequestHeader("access") String access, @RequestBody CardLevelRequestDto requestDto) {
        Long userId = jwtUtil.getUserId(access);
        reportInfoService.setCardLevel(userId, requestDto.getLevel());
        return ResponseEntity.ok(requestDto.getLevel());
    }
}
