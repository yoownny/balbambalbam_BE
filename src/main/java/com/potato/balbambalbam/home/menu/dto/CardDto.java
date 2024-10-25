package com.potato.balbambalbam.home.missedCards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "카드리스트 각 카드 정보 Response")
public class CardDto {
    private Long id;
    private String text;
    private String engPronunciation;
    private int cardScore;
}
