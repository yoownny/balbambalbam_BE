package com.potato.balbambalbam.notice;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.AiGenerationFailException;
import com.potato.balbambalbam.exception.InvalidParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static com.potato.balbambalbam.notice.ScheduledNotificationConstants.*;

@Service
@RequiredArgsConstructor
public class ScheduledNotificationService {

    WebClient webClient = WebClient.builder()
            .codecs(configurer -> configurer
                    .defaultCodecs()
                    .maxInMemorySize(5 * 1024 * 1024)) // 5MB
            .build();
    private final UserRepository userRepository;
    private final UserAttendanceRepository userAttendanceRepository;

    @Value("${ai.service.notice.url}")
    private String AI_URL;

    @Async
    @Scheduled(cron = "0 0 7 * * ?")
    public void sendAbsentNotifications() {
        LocalDate today = LocalDate.now();

        List<Long> absentUsers31Days = getAbsentUsers(31);
        List<Long> absentUsers7Days = getAbsentUsers(7);
        List<Long> absentUsers3Days = getAbsentUsers(3);
        List<Long> absentUsers1Day = getAbsentUsers(1);

        int messageNumber = 0;
        if (today.getDayOfMonth() % 2 == 0) {
            messageNumber = 1;
        }
        sendNotification(absentUsers1Day, ONE_DAY_MESSAGES.get(messageNumber));
        sendNotification(absentUsers3Days, THREE_DAY_MESSAGES.get(messageNumber));
        sendNotification(absentUsers7Days, SEVEN_DAY_MESSAGES.get(messageNumber));
        sendNotification(absentUsers31Days, THIRTY_DAY_MESSAGES.get(messageNumber));
    }

    private void sendNotification(List<Long> absentUsers1Day, String message) {
        for (Long userId : absentUsers1Day) {
            User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("사용자가 없습니다."));
            ScheduledNotificationRequest scheduledNotificationRequest = new ScheduledNotificationRequest(user.getSocialId(), MessageFormat.format(message, user.getName()));
            postNotification(scheduledNotificationRequest);
        }
    }

    private List<Long> getAbsentUsers(int date) {
        return userAttendanceRepository.findUserIdsWithLastAttendanceDaysAgo(date);
    }

    public void postNotification(ScheduledNotificationRequest scheduledNotificationRequest) {
        webClient.post()
                .uri(AI_URL + "/personal_notice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(scheduledNotificationRequest), ScheduledNotificationRequest.class)
                .retrieve()//요청
                .onStatus(HttpStatus.BAD_REQUEST::equals,
                        response -> response.bodyToMono(String.class).map(InvalidParameterException::new))
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals,
                        response -> response.bodyToMono(String.class).map(AiGenerationFailException::new))
                .bodyToMono(ScheduledNotificationResponse.class)
                .timeout(Duration.ofSeconds(10))
                .block();
    }
}
