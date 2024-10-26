package com.potato.balbambalbam.user.home.service;

import com.potato.balbambalbam.data.entity.Level;
import com.potato.balbambalbam.data.entity.TodayCard;
import com.potato.balbambalbam.data.entity.UserAttendance;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.LevelRepository;
import com.potato.balbambalbam.data.repository.TodayCardRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeInfoService {
    private final UserLevelRepository userLevelRepository;
    private final LevelRepository levelRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final TodayCardRepository todayCardRepository;

    @Transactional
    public HomeInfoDto getHomeInfo(Long userId) {
        checkTodayAttendance(userId);
        HomeInfoDto homeInfoDto = new HomeInfoDto();

        setUserLevelInfo(userId, homeInfoDto);
        setUserAttendanceInfo(userId, homeInfoDto);
        setDailyWordInfo(homeInfoDto);

        return homeInfoDto;
    }

    private void checkTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        UserAttendance todayAttendance = userAttendanceRepository
                .findByUserIdAndAttendanceDate(userId, today);

        if (todayAttendance == null) {
            UserAttendance newAttendance = new UserAttendance();
            newAttendance.setUserId(userId);
            newAttendance.setAttendanceDate(today);
            newAttendance.setIsPresent(true);
            userAttendanceRepository.save(newAttendance);
        }
    }

    private void setUserLevelInfo(Long userId, HomeInfoDto homeInfoDto) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found for user: " + userId));

        Level level = levelRepository.findByLevelId(userLevel.getLevelId())
                .orElseThrow(() -> new RuntimeException("Level not found for levelId: " + userLevel.getLevelId()));

        homeInfoDto.setUserLevel(level.getLevel());
        homeInfoDto.setLevelExperience(level.getLevelExperience());
        homeInfoDto.setUserExperience(userLevel.getUserExperience());
    }

    private void setUserAttendanceInfo(Long userId, HomeInfoDto homeInfoDto) {
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<UserAttendance> attendances = userAttendanceRepository.findWeeklyAttendance(userId, monday, sunday);

        char[] weekAttendance = new char[7]; // 월~일까지 7일
        Arrays.fill(weekAttendance, 'F');

        for (UserAttendance attendance : attendances) {
            if (attendance.getIsPresent()) {
                int dayOfWeek = attendance.getAttendanceDate().getDayOfWeek().getValue() - 1;
                weekAttendance[dayOfWeek] = 'T';
            }
        }
        homeInfoDto.setWeeklyAttendance(new String(weekAttendance));
    }

    private void setDailyWordInfo(HomeInfoDto homeInfoDto) {
        // 기준일 설정 (2024-10-26)
        LocalDate baseDate = LocalDate.of(2024, 10, 26);
        LocalDate today = LocalDate.now();

        // 기준일로부터 경과된 일수 계산
        long daysSinceBase = ChronoUnit.DAYS.between(baseDate, today);

        // 전체 카드 개수 (93개)
        final int TOTAL_CARDS = 93;

        // 카드 ID 계산: (경과일 % 전체카드수) + 1
        // 기준일(2024-10-26)의 카드 ID가 1이 되도록 설정
        long todayCardId = (daysSinceBase % TOTAL_CARDS) + 1;

        TodayCard todayCard = todayCardRepository.findById(todayCardId)
                .orElseThrow(() -> new RuntimeException(
                        String.format("Card not found for date %s (ID: %d)", today, todayCardId)));

        homeInfoDto.setDailyWordId(todayCard.getCardId());
        homeInfoDto.setDailyWord(todayCard.getText());
        homeInfoDto.setDailyWordMeaning(todayCard.getCardSummary());
    }

}
