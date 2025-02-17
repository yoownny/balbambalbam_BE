package com.potato.balbambalbam.user.withdrawal.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "탈퇴 사유 Request DTO")
public class WithdrawalRequestDto {

    @NotNull(message = "탈퇴 사유 코드가 필요합니다.")
    private Long reasonCode;
    private String details;
}
