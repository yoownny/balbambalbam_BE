package com.potato.balbambalbam.myReport.weaksound.controller;

import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.exception.dto.ExceptionDto;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.myReport.weaksound.dto.PhonemeResponseDto;
import com.potato.balbambalbam.myReport.weaksound.dto.UserWeakSoundResponseDto;
import com.potato.balbambalbam.myReport.weaksound.service.PhonemeService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@Tag(name = "WeakSound API", description = "사용자의 취약음소와 관련된 API를 제공한다.")
public class PhonemeController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final PhonemeService phonemeService;
    private final PhonemeRepository phonemeRepository;


    @Operation(summary = "사용자의 취약음소 제공", description = "사용자의 취약음소 4개를 제공한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 취약음소가 있는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserWeakSoundResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자의 취약음소가 없는 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/test/phonemes")
    public ResponseEntity<List<UserWeakSoundResponseDto>> getWeakPhonemesByUserId(@RequestHeader("access") String access) {
        Long userId = jwtUtil.getUserId(access);
        List<UserWeakSoundResponseDto> weakPhonemes = phonemeService.getWeakPhonemes(userId);
        if (weakPhonemes.isEmpty()) {
            throw new ResponseNotFoundException("취약음소가 없습니다.");
        }
        return ResponseEntity.ok(weakPhonemes);
    }

    @Operation(summary = "전체 음소 목록과 취약음소 여부 조회", description = "초성, 중성, 종성으로 분류된 전체 음소 목록과 각 음소의 취약음소 여부를 제공한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PhonemeResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @GetMapping("/test/all")
    public ResponseEntity<List<PhonemeResponseDto>> getAllPhonemesWithWeakStatus(@RequestHeader("access") String access) {
        Long userId = jwtUtil.getUserId(access);
        return ResponseEntity.ok(phonemeService.getAllPhonemes(userId));
    }

    @Operation(summary = "취약음소 추가", description = "선택한 음소들을 사용자의 취약음소로 추가한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "추가 성공", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "취약음소가 추가되었습니다."))),
            @ApiResponse(responseCode = "500", description = "서버 오류 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @PostMapping("/test/add")
    public ResponseEntity<?> addWeakPhonemes(@RequestHeader("access") String access, @RequestBody List<Long> phonemeIds) {
        Long userId = jwtUtil.getUserId(access);
        phonemeService.addWeakPhonemes(userId, phonemeIds);
        return ResponseEntity.ok("취약음소가 추가되었습니다.");
    }

    @Operation(summary = "사용자의 개별 취약음소 삭제", description = "사용자의 개별 취약음소를 삭제한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 취약음소가 삭제된 경우", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "사용자의 취약음소가 삭제되었습니다."))),
            @ApiResponse(responseCode = "500", description = "서버 오류가 발생한 경우", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDto.class)))
    })
    @DeleteMapping("test/phonemes/{phonemeId}")
    public ResponseEntity<?> deleteWeakPhoneme(@RequestHeader("access") String access, @PathVariable Long phonemeId) {
        Long userId = jwtUtil.getUserId(access);
        phonemeService.deleteWeakPhoneme(userId, phonemeId);
        return ResponseEntity.ok("사용자의 취약음소가 삭제되었습니다.");
    }
}