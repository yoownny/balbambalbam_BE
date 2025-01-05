package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user_weaksound")
@Getter
@Setter
@IdClass(UserWeakSoundId.class)
public class UserWeakSound {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "user_phoneme", nullable = false)
    private Long userPhoneme;

    public UserWeakSound() {
    }

    public UserWeakSound(Long userId, Long userPhoneme) {
        this.userId = userId;
        this.userPhoneme = userPhoneme;
    }
}
