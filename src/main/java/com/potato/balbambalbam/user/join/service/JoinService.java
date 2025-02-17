package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import com.potato.balbambalbam.user.join.dto.JoinResponseDto;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class JoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final UserLevelRepository userLevelRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CustomCardRepository customCardRepository;
    private final NotificationReadRepository notificationReadRepository;
    private final UserAttendanceRepository userAttendanceRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;

    //새로운 회원정보 저장
    @Transactional
    public void joinProcess(JoinResponseDto joinDto, HttpServletResponse response) {
        String name = joinDto.getName();
        String socialId = joinDto.getSocialId();
        Integer age = joinDto.getAge();
        Byte gender = joinDto.getGender();
        Long level = (joinDto.getLevel() == null) ? 1L : joinDto.getLevel();

        // 사용자 데이터베이스에 회원정보 저장
        User data = new User();
        data.setName(name);
        data.setSocialId(socialId);
        data.setAge(age);
        data.setGender(gender);
        data.setCreatedAt(LocalDateTime.now()); // 생성 시간 설정
        data.setRoleId(2L); // user로 설정
        data.setStatusId(1L); // 활성 상태로 설정
        User savedUser = userRepository.save(data);

        // 사용자 레벨 데이터베이스에 저장
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(savedUser.getId());
        userLevel.setUserExperience(0L);
        userLevel.setLevelId(1L);
        userLevel.setCategoryId(level); // 사용자 카테고리 레벨 설정
        userLevelRepository.save(userLevel);

        // access 토큰 발급
        String access = jwtUtil.createJwt("access", savedUser.getId(), socialId, data.getRoleId(), 7200000L); // 7200000L 120분

        // Refresh 토큰 발급
        String refresh = jwtUtil.createJwt("refresh", savedUser.getId(), socialId, data.getRoleId(), 8640000000L); // 8640000000L 2400시간
        addRefreshEntity(savedUser.getId(), socialId, refresh, 8640000000L);

        response.setHeader("access", access);
        response.setHeader("refresh", refresh);
    }

    private void addRefreshEntity(Long userId, String socialId, String refresh, Long expiredMs) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusSeconds(expiredMs / 1000);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUserId(userId);
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setLastLoginAt(LocalDateTime.now());  // 마지막 로그인 시간 설정
        refreshEntity.setExpiration(expirationTime); // LocalDateTime으로 만료시간 설정

        refreshRepository.save(refreshEntity);
    }

    //회원정보 검색
    public EditResponseDto findUserById(Long userId) {
        // Fetch user
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404

        // 비활성화된 회원인 경우 예외 처리
        if (editUser.getStatusId()==2L) {
            throw new UserNotFoundException("비활성화된 회원입니다.");
        }
        // 탈퇴한 회원인 경우 예외 처리
        if (editUser.getStatusId()==3L) {
            throw new IllegalStateException("탈퇴한 회원입니다.");
        }

        return new EditResponseDto(editUser.getName(), editUser.getAge(), editUser.getGender());
    }

    @Transactional
    public String recoverDeletedUser(String  socialId, HttpServletResponse response) {
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        if (user.getStatusId() != 3L) {
            throw new IllegalStateException("해당 사용자는 탈퇴 상태가 아닙니다.");  // 예외 처리: 탈퇴 상태가 아니면 복구 불가
        }

        // 계정 복구: 상태를 ACTIVE(1)로 변경
        user.setStatusId(1L);  // ACTIVE 상태로 변경
        userRepository.save(user);

        // 복구 시 새 토큰 발급
        String access = jwtUtil.createJwt("access", user.getId(), user.getSocialId(), user.getRoleId(), 7200000L);
        String refresh = jwtUtil.createJwt("refresh", user.getId(), user.getSocialId(), user.getRoleId(), 8640000000L);

        // 토큰을 응답 헤더에 추가
        response.setHeader("access", access);
        response.setHeader("refresh", refresh);

        return "계정이 성공적으로 복구되었습니다.";
    }
    @Transactional
    public void anonymizeUserData(String socialId) {
        // 1. 사용자 조회
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UserNotFoundException("해당 사용자를 찾을 수 없습니다."));

        Long userId = user.getId();

        // 2. 모든 연관된 데이터 삭제
        cardBookmarkRepository.deleteByUserId(userId);
        cardScoreRepository.deleteByUserId(userId);
        customCardRepository.deleteByUserId(userId);
        notificationReadRepository.deleteByUserId(userId);
        userAttendanceRepository.deleteByUserId(userId);
        userLevelRepository.deleteByUserId(userId);
        userWeakSoundRepository.deleteByUserId(userId);
        refreshRepository.deleteByUserId(userId);

        // 3. UUID 생성 (중복 체크)
        String uniqueSocialId;
        do {
            uniqueSocialId = "Deleted_" + UUID.randomUUID();
        } while (userRepository.existsBySocialId(uniqueSocialId));  // DB에 같은 UUID가 있으면 다시 생성

        // 4. 사용자 민감 정보 초기화 (익명화)
        user.setSocialId(uniqueSocialId);
        user.setName("Anonymous");
        user.setStatusId(4L);  // 익명화된 사용자 상태로 설정
        userRepository.save(user);
    }



}