package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user")
@Getter
@Setter
public class User {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "gender", nullable = false)
    private Byte gender; //(남성 : 0, 여성 : 1)

    @Column(name = "social_id", nullable = false, unique = true)
    private String socialId;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public User() {
        this.createdAt = LocalDateTime.now();
    }
}
