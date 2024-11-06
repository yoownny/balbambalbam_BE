package com.potato.balbambalbam.card.cardFeedback.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.Level;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLevelService {
    private final UserLevelRepository userLevelRepository;
    private final LevelRepository levelRepository;
    private final CustomCardRepository customCardRepository;
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;

    public void updateUserLevelInfo(Long cardId, Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        //경험치 update
        updateUserExperience(cardId, userLevel);
        //레벨 체크
        updateUserLevel(userLevel);
    }

    public void updateCustomCardUserLevelInfo(Long cardId, Long userId) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        //경험치 update
        updateUserCustomExperience(cardId, userLevel);
        //레벨 체크
        updateUserLevel(userLevel);
    }

    @Transactional
    protected void updateUserLevel(UserLevel userLevel) {
        Level currentLevel = levelRepository.findByLevelId(userLevel.getLevelId()).get();
        if(userLevel.getLevelId() == 25) return;
        if(currentLevel.getLevelExperience() <= userLevel.getUserExperience()) {
            userLevel.setLevelId(userLevel.getLevelId() + 1);
            userLevel.setUserExperience(0L);
            userLevelRepository.save(userLevel);
        }
    }

    @Transactional
    protected void updateUserExperience(Long cardId, UserLevel userLevel) {
        Long categoryId = cardRepository.findByCardId(cardId).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다.")).getCategoryId();

        if(cardScoreRepository.existsByCardIdAndUserId(cardId, userLevel.getUserId())) return;

        int experience;
        if (categoryId < 5) experience = 1;
        else if (categoryId < 15) experience = 5;
        else experience = 10;
        userLevel.setUserExperience(userLevel.getUserExperience() + experience);
        userLevelRepository.save(userLevel);
    }

    @Transactional
    protected void updateUserCustomExperience(Long cardId, UserLevel userLevel) {
        CustomCard customCard = customCardRepository.findById(cardId).get();
        if(customCard.getHighestScore() != null) return;
        int experience = 10;
        userLevel.setUserExperience(userLevel.getUserExperience() + experience);
        userLevelRepository.save(userLevel);
    }
}
