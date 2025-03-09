package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserAttendance;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    @Query("SELECT ua FROM user_attendance ua " +
            "WHERE ua.userId = :userId AND ua.attendanceDate = :attendanceDate")
    UserAttendance findByUserIdAndAttendanceDate(@Param("userId") Long userId,
                                                 @Param("attendanceDate") LocalDate attendanceDate);

    @Query("SELECT ua FROM user_attendance ua " +
            "WHERE ua.userId = :userId " +
            "AND ua.attendanceDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ua.attendanceDate")
    List<UserAttendance> findWeeklyAttendance(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    boolean existsByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM user_attendance ua WHERE ua.userId = :userId")
    void deleteByUserId(Long userId);

    @Query("SELECT ua FROM user_attendance ua WHERE ua.userId = :userId AND ua.isPresent = true")
    List<UserAttendance> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(ua) FROM user_attendance ua WHERE ua.userId = :userId AND ua.isPresent = :isPresent")
    Long countByUserIdAndIsPresent(@Param("userId") Long userId, @Param("isPresent") Boolean isPresent);

    Optional<UserAttendance> findTopByUserIdAndAttendanceDateBeforeOrderByAttendanceDateDesc(Long userId, LocalDate today);

    @Query(value = "SELECT user_id as id " +
            "FROM (SELECT user_id, MAX(attendance_date) as last_attendance_date " +
            "      FROM user_attendance " +
            "      GROUP BY user_id) as latest_attendance " +
            "WHERE last_attendance_date = CURRENT_DATE - INTERVAL :days DAY",
            nativeQuery = true)
    List<Long> findUserIdsWithLastAttendanceDaysAgo(@Param("days") int days);
}