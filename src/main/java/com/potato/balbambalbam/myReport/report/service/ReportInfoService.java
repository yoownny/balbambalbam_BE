package com.potato.balbambalbam.myReport.report.service;

import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.myReport.report.dto.ReportInfoDto;
import com.potato.balbambalbam.myReport.weaksound.service.PhonemeService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportInfoService {
    private final UserRepository userRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final CardScoreRepository cardScoreRepository;
    private final PhonemeService phonemeService;
    private final UserLevelRepository userLevelRepository;
    private final CardRepository cardRepository;

    @Transactional(readOnly = true)
    public ReportInfoDto getMyReportInfo(Long userId) {
        ReportInfoDto reportInfoDto = new ReportInfoDto();

        // 1. 사용자 기본 정보 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        reportInfoDto.setNickname(user.getName());

        // 2. 출석 체크한 총 일수
        Long studyDays = userAttendanceRepository.countByUserIdAndIsPresent(userId, true);
        reportInfoDto.setStudyDays(studyDays);

        // 3. 전체 학습 통계
        // 총 학습 카드 수
        List<CardScore> allCardScores = cardScoreRepository.findByUserId(userId);
        reportInfoDto.setTotalLearned((long) allCardScores.size());

        // 전체 학습 정확도 - 소수점 첫째자리까지 반올림
        Double accuracy = cardScoreRepository.getAverageScoreByUserId(userId);
        double roundedAccuracy = accuracy != null ? Math.round(accuracy * 10) / 10.0 : 0.0;
        reportInfoDto.setAccuracy(roundedAccuracy);

        // 4. 이번 주 요일별 학습 카드 수 계산
        LocalDate today = LocalDate.now();

        // 이전 주 일요일 찾기 (오늘이 일요일이면 오늘이 시작일)
        LocalDate startOfWeek = today.getDayOfWeek() == DayOfWeek.SUNDAY ?
                today : today.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));

        long daysFromSunday = ChronoUnit.DAYS.between(startOfWeek, today) + 1;

        // 요일별 카드 수를 저장할 Map 초기화
        Map<DayOfWeek, Long> cardsByDay = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            cardsByDay.put(day, 0L);
        }

        List<CardScore> weeklyCardScores = cardScoreRepository.findByUserIdAndTimeStampBetween(
                userId,
                startOfWeek.atStartOfDay(),
                today.atTime(23, 59, 59)
        );

        weeklyCardScores.forEach(score -> {
            DayOfWeek dayOfWeek = score.getTimeStamp().getDayOfWeek();
            cardsByDay.merge(dayOfWeek, 1L, Long::sum);
        });

        // 각 요일별 카드 수 설정
        reportInfoDto.setSundayCards(cardsByDay.get(DayOfWeek.SUNDAY));
        reportInfoDto.setMondayCards(cardsByDay.get(DayOfWeek.MONDAY));
        reportInfoDto.setTuesdayCards(cardsByDay.get(DayOfWeek.TUESDAY));
        reportInfoDto.setWednesdayCards(cardsByDay.get(DayOfWeek.WEDNESDAY));
        reportInfoDto.setThursdayCards(cardsByDay.get(DayOfWeek.THURSDAY));
        reportInfoDto.setFridayCards(cardsByDay.get(DayOfWeek.FRIDAY));
        reportInfoDto.setSaturdayCards(cardsByDay.get(DayOfWeek.SATURDAY));

        // 5. 이번 주 평균 카드 수 계산
        long totalWeeklyCards = weeklyCardScores.size();
        reportInfoDto.setWeeklyAverageCards(daysFromSunday > 0 ? totalWeeklyCards / daysFromSunday : 0);

        // 6. 취약 음소 목록 조회
        reportInfoDto.setWeakPhonemes(phonemeService.getWeakPhonemes(userId));

        // 7. 사용자 레벨 조회
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User level not found"));
        Long categoryId = userLevel.getCategoryId();
        reportInfoDto.setCardLevel(getLevelNameByCategoryId(categoryId));

        return reportInfoDto;
    }

    private String getLevelNameByCategoryId(Long categoryId) {
        if (categoryId >= 1 && categoryId <= 4) {
            return "Beginner";
        } else if (categoryId >= 5 && categoryId <= 15) {
            return "Intermediate";
        } else {
            return "Advanced";
        }
    }

    @Transactional
    public void setCardLevel(Long userId, String level) {
        // 1. 사용자 레벨 조회
        UserLevel userLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. 선택한 레벨에 따라 초기 카테고리 ID 설정
        Long initialCategoryId;
        switch (level.toLowerCase()) {
            case "beginner":
                initialCategoryId = 1L;
                break;
            case "intermediate":
                initialCategoryId = 5L;
                break;
            case "advanced":
                initialCategoryId = 16L;
                break;
            default:
                throw new IllegalArgumentException("Invalid level");
        }

        // 3. 학습한 카테고리를 확인하고 다음 카테고리로 이동
        Long nextCategoryId = getNextCategoryId(userId, initialCategoryId);
        userLevel.setCategoryId(nextCategoryId);
        userLevelRepository.save(userLevel);
    }

    private Long getNextCategoryId(Long userId, Long initialCategoryId) {
        Long currentCategoryId = initialCategoryId;

        // 1. 사용자가 학습한 모든 카드 ID와 모든 카테고리의 카드 ID를 한 번에 가져오기
        List<Long> studiedCardIds = cardScoreRepository.findByUserId(userId).stream().map(CardScore::getCardId).toList();
        Map<Long, List<Long>> allCategoryCards = cardRepository.findAll().stream()
                .collect(Collectors.groupingBy(Card::getCategoryId, Collectors.mapping(Card::getCardId, Collectors.toList())));

        // 2. 현재 카테고리부터 시작하여 모든 카드를 학습했는지 검사
        while (allCategoryCards.containsKey(currentCategoryId)) {
            List<Long> currentCategoryCards = allCategoryCards.get(currentCategoryId);

            long studiedCount = currentCategoryCards.stream().filter(studiedCardIds::contains).count();

            // 모든 카드를 학습했다면 다음 카테고리로 이동
            if (studiedCount == currentCategoryCards.size()) {
                currentCategoryId++;
            } else {
                break;
            }
        }

        return currentCategoryId;
    }

}