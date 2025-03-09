package com.potato.balbambalbam.notice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class NotificationServiceTest {
    @Autowired
    private ScheduledNotificationService notificationService;

    @Test
    void testSendAbsentNotifications() {
        ReflectionTestUtils.invokeMethod(notificationService, "sendAbsentNotifications");
    }
}
