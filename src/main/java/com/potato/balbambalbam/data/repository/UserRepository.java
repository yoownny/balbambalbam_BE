package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialId(String socialId);
    Boolean existsBySocialId(String SocialId);
}