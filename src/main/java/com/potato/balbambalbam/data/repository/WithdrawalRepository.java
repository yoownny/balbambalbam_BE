package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
}
