package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Refresh;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {
    Boolean existsByRefresh(String refresh);

    @Query("SELECT r.refresh FROM refresh r WHERE r.socialId = :socialId")
    String findRefreshBySocialId(@Param("socialId") String socialId);

    @Transactional
    @Modifying
    @Query("DELETE FROM refresh r WHERE r.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Transactional
    @Modifying
    @Query("DELETE FROM refresh r WHERE r.socialId = :socialId AND r.userId = :userId")
    void deleteBySocialIdAndUserId(@Param("socialId") String socialId, @Param("userId") Long userId);

    @Transactional
    void deleteByRefresh(String refresh);
}