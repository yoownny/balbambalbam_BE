package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "status")
@Getter
@Setter
public class Status {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "status", nullable = false)
    private String status;
}
