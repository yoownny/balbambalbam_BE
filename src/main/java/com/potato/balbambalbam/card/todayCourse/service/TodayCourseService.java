package com.potato.balbambalbam.card.todayCourse.service;

import com.potato.balbambalbam.card.todayCourse.dto.TodayCourseRequestDto;
import com.potato.balbambalbam.card.todayCourse.dto.TodayCourseResponseDto;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodayCourseService {
    private final UserLevelRepository userLevelRepository;
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;

    public TodayCourseResponseDto getCardList(Long userId, TodayCourseRequestDto todayCourseRequestDto) {
        //1. 카드 레벨을 가져온다
        Integer courseSize = todayCourseRequestDto.getCourseSize();
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));

        //2. 카드를 가져온다
        long categoryId = userLevel.getCategoryId();
        List<Long> cardList;
        if (categoryId >= 1 && categoryId <= 4) { //음절
            cardList = getSyllableOrSentenceList(userId, categoryId, courseSize);
        } else if (categoryId >= 5 && categoryId <= 14) { //단어
            cardList = getWordList(userId, categoryId, courseSize);
        } else if (categoryId >= 15 && categoryId <= 22) { //문장
            cardList = getSyllableOrSentenceList(userId, categoryId, courseSize);
        } else { // 만렙
            cardList = getRandomCardList(userId, courseSize);
        }

        return new TodayCourseResponseDto(cardList);
    }

    protected List<Long> getSyllableOrSentenceList(Long userId, Long categoryId, int courseSize) {
        List<Long> userCardList = cardScoreRepository.findByUserId(userId).stream().map(CardScore::getCardId).toList();

        int idx = 0;
        int currentSize = 0;
        List<Long> todayCourseList = new ArrayList<>();
        while (currentSize < courseSize) {
            List<Long> cardList = cardRepository.findAllByCategoryId(categoryId + (idx++)).stream().map(Card::getCardId)
                    .toList();
            Long nextCardId = getNextCardId(cardList, userCardList);
            todayCourseList.addAll(
                    cardList.stream().filter(id -> !userCardList.contains(id)).filter(id -> id >= nextCardId)
                            .limit(courseSize - currentSize).toList());
            currentSize += todayCourseList.size();
            if (categoryId + idx > 25) {
                break;
            }
        }

        return todayCourseList;
    }

    protected List<Long> getWordList(Long userId, Long categoryId, int courseSize) {
        List<Long> userCardList = cardScoreRepository.findByUserId(userId).stream().map(CardScore::getCardId).toList();
        List<Long> cardList = cardRepository.findAllByCategoryId(categoryId).stream().map(Card::getCardId).toList();

        Long nextCardId = getNextCardId(cardList, userCardList);
        cardList.stream().filter(id -> !userCardList.contains(id)).filter(id -> id >= nextCardId).toList();

        return pickRandomCard(cardList, courseSize);
    }

    protected List<Long> getRandomCardList(Long userId, int courseSize) {
        List<Long> userCardList = cardScoreRepository.findByUserId(userId).stream().map(CardScore::getCardId).toList();
        List<Long> cardList = cardRepository.findAll().stream().map(Card::getCardId)
                .filter(id -> !userCardList.contains(id)).toList();

        return pickRandomCard(cardList, courseSize);
    }

    //최근에 학습한 아이디 return
    protected Long getNextCardId(List<Long> cardList, List<Long> userCardList) {
        if (cardList.isEmpty()) {
            return null;
        }

        Set<Long> intersection = cardList.stream()
                .filter(userCardList::contains)
                .collect(Collectors.toSet());

        if (intersection.isEmpty()) {
            return cardList.stream().min(Long::compareTo).get();
        }

        return intersection.stream().min(Long::compareTo).get();
    }

    //카드 랜덤 추출
    protected List<Long> pickRandomCard(List<Long> cards, int courseSize) {
        Random random = new Random();
        List<Long> randomList = new ArrayList<>();
        int[] indices = IntStream.range(0, cards.size()).toArray();
        for (int i = 0; i < courseSize; ++i) {
            int randomIndex = random.nextInt(cards.size() - i);
            randomList.add(cards.get(indices[randomIndex]));
            indices[randomIndex] = indices[cards.size() - 1 - i];
        }

        return randomList;
    }
}