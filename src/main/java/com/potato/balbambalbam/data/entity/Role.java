package com.potato.balbambalbam.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "role")
@Getter
@Setter
public class Role {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "role", nullable = false)
    private String role;
}
