package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "withdrawal_code")
@Getter
@Setter
public class WithdrawalCode {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int code;

    @Column(name = "description", nullable = false)
    private String description;
}
