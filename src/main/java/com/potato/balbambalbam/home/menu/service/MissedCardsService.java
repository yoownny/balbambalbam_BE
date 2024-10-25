package com.potato.balbambalbam.home.missedCards.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.repository.CardBookmarkRepository;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.CardWeakSoundRepository;
import com.potato.balbambalbam.home.missedCards.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissedCardsService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;

    public Map<String, List<CardDto>> getCards(Long userId){
        return createCardDtoListForCategory(userId);
    }

    protected Map<String, List<CardDto>> createCardDtoListForCategory(Long userId){
        List<CardScore> missedCardsByUserId = cardScoreRepository.findByUserId(userId);
        List<CardDto> cardDtoList = new ArrayList<>();

        for(CardScore cardScore : missedCardsByUserId) {
            cardScore.getTimeStamp().getDayOfYear();
            cardDtoList.add(convertCardToDto(cardScore));
        }

        System.out.println(LocalDateTime.now());
        return null;
    }


    protected CardDto convertCardToDto(CardScore cardScore){
        CardDto cardDto = new CardDto();

        Long userId = cardScore.getUserId();
        Long cardId = cardScore.getId();
        Card card = cardRepository.findByCardId(cardId).get();
        cardDto.setId(cardId);
        cardDto.setText(card.getText());

        cardDto.setCardScore(cardScore.getHighestScore());  //사용자 점수가 없으면 0점
        cardDto.setEngPronunciation(card.getCardPronunciation());

        return cardDto;
    }
}
