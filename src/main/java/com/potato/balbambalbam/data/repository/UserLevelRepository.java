package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    UserLevel findByUserId(long userId);
    boolean existsByUserId(Long userId);
    void deleteByUserId(Long userId);
}
