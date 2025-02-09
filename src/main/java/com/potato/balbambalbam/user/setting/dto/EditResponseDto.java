package com.potato.balbambalbam.user.setting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "변경 사용자 정보 Response")
public class EditResponseDto {

    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private String name;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Integer age;
    @NotNull(message = "입력 데이터가 충분하지 않습니다.")
    private Byte gender;

    public EditResponseDto(String name, Integer age, Byte gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }
}
