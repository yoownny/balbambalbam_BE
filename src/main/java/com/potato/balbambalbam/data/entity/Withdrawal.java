package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "withdrawal")
@Getter
@Setter
public class Withdrawal {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "code", nullable = false)
    private long code;

    @Column(name = "details")
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
