package com.potato.balbambalbam.card.cardInfo.service;

import com.potato.balbambalbam.card.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.card.cardInfo.dto.AiTtsResponseDto;
import com.potato.balbambalbam.card.cardInfo.dto.TodayCardInfoResponseDto;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TodayCardInfoService {

    private final TodayCardRepository todayCardRepository;
    private final UserRepository userRepository;
    private final AiTtsService aiTtsService;

    public TodayCard getTodayCardEntity() {
        final int cycleLength = 93;
        LocalDate startDate = LocalDate.of(2024, 10, 25);
        long daysBetween = ChronoUnit.DAYS.between(startDate, LocalDate.now());

        long todayCardId = (daysBetween % cycleLength) + 1;
        return todayCardRepository.findById(todayCardId).get();
    }

    public TodayCardInfoResponseDto getTodayCardInfo(Long userId, Long cardId) {
        TodayCard todayCard = todayCardRepository.findById(cardId).get();
        String voice = createCardVoice(getAiTtsRequestDto(userId, todayCard.getText()));
        return getCardInfoResponseDto(userId, cardId, voice);
    }

    protected TodayCardInfoResponseDto getCardInfoResponseDto(Long userId, Long cardId, String voice) {
        TodayCard card = todayCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));

        TodayCardInfoResponseDto todayCardInfoResponseDto = TodayCardInfoResponseDto.builder()
                .id(card.getCardId())
                .text(card.getText())
                .cardPronunciation(card.getCardPronunciation())
                .cardSummary(card.getCardSummary())
                .correctAudio(voice)
                .build();
        return todayCardInfoResponseDto;
    }

    protected AiTtsRequestDto getAiTtsRequestDto(Long userId, String text) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Byte gender = user.getGender();

        return new AiTtsRequestDto(age, gender, text);
    }

    protected String createCardVoice(AiTtsRequestDto aiTtsRequestDto) {
        AiTtsResponseDto aiTtsResponseDto = aiTtsService.getTtsVoice(aiTtsRequestDto);
        return aiTtsResponseDto.getCorrectAudio();
    }
}