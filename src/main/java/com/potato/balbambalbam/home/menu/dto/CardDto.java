package com.potato.balbambalbam.home.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카드리스트 각 카드 정보 Response")
public class CardDto {

    @Schema(name = "card Id", example = "1")
    private Long id;

    @Schema(name = "card 내용", example = "아")
    private String text;

    @Schema(name = "card 로마자 발음", example = "a")
    private String cardPronunciation;

    @Schema(name = "card 북마크 유뮤", example = "true")
    private boolean isBookmarked;

    @Schema(name = "card 취약음 유뮤", example = "true")
    private boolean isWeakCard;

    @Schema(name = "card 최고 점수", example = "80")
    private int cardScore;
}
