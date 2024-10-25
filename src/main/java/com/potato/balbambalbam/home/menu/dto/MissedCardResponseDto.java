package com.potato.balbambalbam.home.missedCards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "missedCard 리스트 Response")
public class MissedCardResponseDto<T> {
    private Map<String, T> cardList;
    private int count;

    public MissedCardResponseDto(Map<String, T> cardList, int count) {
        this.cardList = cardList;
        this.count = count;
    }

}
