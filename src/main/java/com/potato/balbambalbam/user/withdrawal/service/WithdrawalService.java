package com.potato.balbambalbam.user.withdrawal.service;

import com.potato.balbambalbam.data.entity.Withdrawal;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.data.repository.WithdrawalRepository;
import com.potato.balbambalbam.user.withdrawal.dto.WithdrawalRequestDto;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;

    public void processWithdrawal(Long userId, WithdrawalRequestDto requestDto) {

        // 탈퇴 사유가 'Other(6)'일 경우, details가 필수
        if (requestDto.getReasonCode() == 6 && (requestDto.getDetails() == null || requestDto.getDetails().isBlank())) {
            throw new IllegalArgumentException("Other인 경우 입력값이 필요합니다.");
        }

        // 탈퇴 기록 저장
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setCode(requestDto.getReasonCode());
        withdrawal.setDetails(requestDto.getReasonCode() == 6 ? requestDto.getDetails() : null);
        withdrawal.setCreatedAt(LocalDateTime.now());

        withdrawalRepository.save(withdrawal);
    }
}
