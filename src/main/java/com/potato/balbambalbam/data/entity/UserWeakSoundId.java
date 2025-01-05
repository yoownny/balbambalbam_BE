package com.potato.balbambalbam.data.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserWeakSoundId implements Serializable {
    private Long userId;
    private Long userPhoneme;
}
