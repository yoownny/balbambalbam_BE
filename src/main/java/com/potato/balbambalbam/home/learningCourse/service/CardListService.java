package com.potato.balbambalbam.home.learningCourse.service;

import com.potato.balbambalbam.data.entity.*;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.home.customCard.dto.CustomCardDto;
import com.potato.balbambalbam.home.learningCourse.dto.CourseResponseDto;
import com.potato.balbambalbam.home.learningCourse.dto.ResponseCardDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardListService {
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CustomCardRepository customCardRepository;
    private final PronunciationPictureRepository pronunciationPictureRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;

    /**
     * controller getCardList 요청 처리
     *
     * @return cardDtoList
     */
    public List<ResponseCardDto> getCardsByCategory(Long categoryId, Long userId) {
        List<ResponseCardDto> cardDtoList = createCardDtoListForCategory(categoryId, userId);
        return cardDtoList;
    }

    public List<CustomCardDto> getCustomCards(Long userId) {
        List<CustomCard> customCardList = customCardRepository.findAllByUserId(userId);
        List<CustomCardDto> cardDtoList = new ArrayList<>();

        customCardList.forEach(customCard -> {
            int highestScore = (customCard.getHighestScore() == null) ? 0 : customCard.getHighestScore();
            cardDtoList.add(new CustomCardDto
                    (customCard.getId(), customCard.getText(), customCard.getEngTranslation(), customCard.getEngPronunciation(),
                            customCard.getIsBookmarked(), false, highestScore,
                            customCard.getTimeStamp()));
        });

        return cardDtoList;
    }

    /**
     * 카테고리에 맞는 카드 DTO 리스트 반환
     */
    protected List<ResponseCardDto> createCardDtoListForCategory(Long categoryId, Long userId) {
        List<Card> cardList = cardRepository.findAllByCategoryId(categoryId);
        List<ResponseCardDto> cardDtoList = new ArrayList<>();

        List<Long> userWeakSounds = userWeakSoundRepository.findAllByUserId(userId).stream().map(UserWeakSound::getUserPhoneme).toList();
        cardList.forEach(card -> cardDtoList.add(convertCardToDto(card, userId, userWeakSounds)));

        return cardDtoList;
    }

    /**
     * Card Entity를 ResponseCardDto에 맞게 변환
     *
     * @param card
     * @param userWeakSounds
     * @return
     */
    protected ResponseCardDto convertCardToDto(Card card, Long userId, List<Long> userWeakSounds) {
        ResponseCardDto responseCardDto = new ResponseCardDto();

        Long cardId = card.getCardId();
        responseCardDto.setId(cardId);
        responseCardDto.setText(card.getText());

        responseCardDto.setCardScore(cardScoreRepository.findByCardIdAndUserId(cardId, userId).map(CardScore::getHighestScore).orElse(0));  //사용자 점수가 없으면 0점
        responseCardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId));
        responseCardDto.setEngTranslation(card.getCardTranslation());
        responseCardDto.setEngPronunciation(card.getCardPronunciation());

        //취약음 확인
        responseCardDto.setWeakCard(isWeakSoundCard(card.getPhonemesMap(), userWeakSounds));

        //level 1 이라면 사진과 설명 제공
        if (card.getCategoryId() == 1) {
            Long phonemeId = null;
            //모음인 경우
            if (card.getCardId() <= 26) {
                phonemeId = card.getPhonemesMap().get(1);
            }
            //자음인 경우
            else {
                phonemeId = card.getPhonemesMap().get(0);
            }
            PhonemeExplanation phonemeExplanation = pronunciationPictureRepository.findByPhonemeId(phonemeId).orElseThrow(() -> new IllegalArgumentException("음절 설명 찾기에 실패했습니다"));
            responseCardDto.setPictureUrl("/images/" + phonemeId + ".png");
            responseCardDto.setExplanation(phonemeExplanation.getExplanation());
        } else {
            responseCardDto.setPictureUrl(null);
            responseCardDto.setExplanation(null);
        }

        return responseCardDto;
    }

    private boolean isWeakSoundCard(List<Long> cardPhonemes, List<Long> userPhonemes) {
        return cardPhonemes.stream().anyMatch(userPhonemes::contains);
    }


    /**
     * 카드 북마크 업데이트
     *
     * @param cardId
     * @param userId
     */
    public String toggleCardBookmark(Long cardId, Long userId) {
        cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다."));

        if (cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId)) {
            cardBookmarkRepository.deleteByCardIdAndUserId(cardId, userId);
            return cardId + "번 카드 북마크 제거";
        } else {
            CardBookmark cardBookmark = new CardBookmark(userId, cardId);
            cardBookmarkRepository.save(cardBookmark);
            return cardId + "번 카드 북마크 추가";
        }
    }

    public String toggleCustomCardBookmark(Long cardId) {
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다."));

        if (customCard.getIsBookmarked()) {
            customCard.setIsBookmarked(false);
            customCardRepository.save(customCard);
            return cardId + "번 카드 북마크 제거";
        } else {
            customCard.setIsBookmarked(true);
            customCardRepository.save(customCard);
            return cardId + "번 카드 북마크 추가";
        }
    }

    public CourseResponseDto getCourseList(Long userId) {
        List<CourseResponseDto.Course> courses = new ArrayList<>();

        List<Long> completedIds = cardScoreRepository.findByUserId(userId).stream().map(CardScore::getCardId).toList();
        for(long i = 1; i <= 25; i++) {
            List<Long> cardIds = cardRepository.findAllByCategoryId(i).stream().map(Card::getCardId).toList();
            int count = (int)completedIds.stream().filter(cardIds::contains).count();

            courses.add(new CourseResponseDto.Course(i, cardIds.size(), count));
        }

        return new CourseResponseDto(courses);
    }
}
