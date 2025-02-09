package com.potato.balbambalbam.myReport.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "카드 레벨 설정 요청 DTO")
public class CardLevelRequestDto {
    @Schema(description = "선택한 레벨 (Beginner, Intermediate, Advanced)")
    private String level;
}
