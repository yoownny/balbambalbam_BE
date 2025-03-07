package com.potato.balbambalbam.notice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScheduledNotificationRequest {
    private String user;
    private String text;
}
