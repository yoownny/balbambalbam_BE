package com.potato.balbambalbam.user.setting.service;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.setting.dto.EditResponseDto;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProfileService {
    private final UserLevelRepository userLevelRepository;
    private final UserRepository userRepository;

    // 회원정보 업데이트
    @Transactional
    public EditResponseDto updateUser(Long userId, EditResponseDto editResponseDto) {
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404

        UserLevel editUserLevel = userLevelRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자 레벨 정보를 찾을 수 없습니다."));

        editUser.setName(editResponseDto.getName());
        editUser.setAge(editResponseDto.getAge());
        editUser.setGender(editResponseDto.getGender());

        if (editResponseDto.getLevel() != null) {
            editUserLevel.setCategoryId(editResponseDto.getLevel());
        }

        userRepository.save(editUser);
        userLevelRepository.save(editUserLevel);

        return new EditResponseDto(editUser.getName(), editUser.getAge(), editUser.getGender(),editUserLevel.getCategoryId());
    }

    //회원정보 삭제
    @Transactional
    public void deleteUser(Long userId, String name) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        user.setStatusId(3L);
    }

    public User findUserBySocialId(String socialId) {
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다.")); //404
    }

}
