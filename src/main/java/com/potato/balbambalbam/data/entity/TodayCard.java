package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "today_card")
@NoArgsConstructor
@Getter
public class TodayCard {
    @Id
    @Column(name = "card_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId;

    @Column(name = "text")
    private String text;

    @Column(name = "card_pronunciation")
    private String cardPronunciation;

    @Column(name = "card_summary")
    private String cardSummary;
}