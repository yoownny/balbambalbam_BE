package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "refresh")
@Getter
@Setter
public class Refresh {
    @Id
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "social_id", nullable = false)
    private String socialId;
    @Column(name = "refresh", nullable = false)
    private String refresh;
    @Column(name = "expiration", nullable = false)
    private String expiration;
}