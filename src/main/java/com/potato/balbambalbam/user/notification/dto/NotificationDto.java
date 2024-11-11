package com.potato.balbambalbam.user.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Notification 목록 조회")
public class NotificationDto {

    @Schema(description = "알림 ID", example = "1")
    private Long id;
    @Schema(description = "알림 제목", example = "App updated")
    private String title;
    @Schema(description = "알림 내용", example = "Bug fixes and new updates have been completed.")
    private String content;
    @Schema(description = "생성 시간", example = "2024-01-21 14:30:00")
    private LocalDateTime createdAt;
    @Schema(description = "활성화 여부", example = "true")
    private boolean isActive;
}
