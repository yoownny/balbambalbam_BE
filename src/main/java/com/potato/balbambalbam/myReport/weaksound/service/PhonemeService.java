package com.potato.balbambalbam.myReport.weaksound.service;

import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.entity.WeakSoundTestStatus;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.data.repository.WeakSoundTestSatusRepositoy;
import com.potato.balbambalbam.exception.ResponseNotFoundException;
import com.potato.balbambalbam.myReport.test.dto.TestResponseDto;
import com.potato.balbambalbam.myReport.weaksound.dto.UserWeakSoundResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhonemeService {
    private final PhonemeRepository phonemeRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final WeakSoundTestSatusRepositoy weakSoundTestSatusRepositoy;
    private Map<Long, Map<Long, Integer>> temporaryStorage = new HashMap<>();


    @Transactional
    public void storePhonemeData(Long userId, TestResponseDto dto) { //임시 저장소
        Map<Long, Integer> phonemeCounts = temporaryStorage.getOrDefault(userId, new HashMap<>());
        Map<Long, Integer> newPhonemeCounts = new HashMap<>();

        // UserWeakPhoneme -> type(0,1), UserWeakPhonemeLast -> type(2)
        phonemeMap(dto.getUserWeakPhoneme(), Arrays.asList(0L, 1L), newPhonemeCounts);
        phonemeMap(dto.getUserWeakPhonemeLast(), Collections.singletonList(2L), newPhonemeCounts);

        newPhonemeCounts.forEach((key, value) -> phonemeCounts.merge(key, value, Integer::sum));
        temporaryStorage.put(userId, phonemeCounts);
    }

    private void phonemeMap(Map<String, Integer> testResponseMap, List<Long> types,
                            Map<Long, Integer> newPhonemeCounts) {
        for (Map.Entry<String, Integer> entry : testResponseMap.entrySet()) {
            List<Phoneme> phonemes = phonemeRepository.findByTypeAndText(types, entry.getKey());
            phonemes.forEach(phoneme -> newPhonemeCounts.merge(phoneme.getId(), entry.getValue(), Integer::sum));
        }
    }

    public Map<Long, Integer> getTopPhonemes(Long userId) {
        return temporaryStorage.getOrDefault(userId, new HashMap<>())
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(4)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void clearTemporaryData(Long userId) {
        temporaryStorage.remove(userId);
    }

    @Transactional
    public void deleteWeakPhoneme(Long userId, Long phonemeId) {
        UserWeakSound userWeakSound = userWeakSoundRepository
                .findByUserIdAndUserPhoneme(userId, phonemeId)
                .orElseThrow(() -> new ResponseNotFoundException("해당하는 취약음소가 없습니다."));
        userWeakSoundRepository.delete(userWeakSound);
    }

    @Transactional
    public void deleteAllWeakPhonemesAndStatus(Long userId) {
        // 모든 취약음소 삭제
        List<UserWeakSound> userWeakSounds = userWeakSoundRepository.findAllByUserId(userId);
        if (!userWeakSounds.isEmpty()) {
            userWeakSoundRepository.deleteAll(userWeakSounds);
        }
        // 테스트 상태 삭제
        WeakSoundTestStatus testStatus = weakSoundTestSatusRepositoy.findByUserId(userId);
        if (testStatus != null) {
            weakSoundTestSatusRepositoy.delete(testStatus);
        }
    }

    @Transactional
    public List<UserWeakSoundResponseDto> getWeakPhonemes(Long userId) {
        List<UserWeakSound> weakPhonemes = userWeakSoundRepository.findAllByUserId(userId);
        if (weakPhonemes.isEmpty()) {
            return List.of();
        }

        return weakPhonemes.stream()
                .map(weakPhoneme -> {
                    Phoneme phoneme = phonemeRepository.findById(weakPhoneme.getUserPhoneme())
                            .orElseThrow(() -> new RuntimeException("음소 정보를 찾을 수 없습니다."));
                    String phonemeText = getPhonemeType(phoneme.getType()) + " " + phoneme.getText();
                    return new UserWeakSoundResponseDto(
                            weakPhonemes.indexOf(weakPhoneme) + 1,
                            phoneme.getId(),
                            phonemeText
                    );
                })
                .collect(Collectors.toList());
    }
    private String getPhonemeType(Long type) {
        return switch (type.intValue()) {
            case 0 -> "Initial consonant";
            case 1 -> "Medial vowel";
            case 2 -> "Final consonant";
            default -> "?";
        };
    }
}
