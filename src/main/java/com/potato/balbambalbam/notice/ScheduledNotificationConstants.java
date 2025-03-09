package com.potato.balbambalbam.notice;

import java.util.List;

public class ScheduledNotificationConstants {

    public static final String ONE_DAY_MESSAGE_1 = "*Missing a day, {0}?*\nNot on our watch! Let’s practice a little.⏳";
    public static final String ONE_DAY_MESSAGE_2 = "*{0}, 1 day off is fine…*But let’s not make it two! Let’s keep the momentum going.🚀";
    public static final List<String> ONE_DAY_MESSAGES = List.of(ONE_DAY_MESSAGE_1, ONE_DAY_MESSAGE_2);

    public static final String THREE_DAY_MESSAGE_1 = "*Uh-oh… {0}’s Korean skills are getting lonely!\nTime to practice before they run away!🥹";
    public static final String THREE_DAY_MESSAGE_2 = "*{0} progress is taking a nap.💤\nTime to wake it up with a lesson!";
    public static final List<String> THREE_DAY_MESSAGES = List.of(THREE_DAY_MESSAGE_1, THREE_DAY_MESSAGE_2);

    public static final String SEVEN_DAY_MESSAGE_1 = "*7 days without practice, {0}?*\nThat’s like a whole vacation🏝️.But now it’s time to get back on track!";
    public static final String SEVEN_DAY_MESSAGE_2 = "*Hello, {0}… Did you forget about me?🥺Let’s reunite for a quick lesson!";
    public static final List<String> SEVEN_DAY_MESSAGES = List.of(SEVEN_DAY_MESSAGE_1, SEVEN_DAY_MESSAGE_2);

    public static final String THIRTY_ONE_DAY_MESSAGE_1 = "*{0}, Balbam here… It’s been forever!🥲\nCome back, and let’s get back to learning together!";
    public static final String THIRTY_ONE_DAY_MESSAGE_2 = "*🚨Breaking news: {0}’ve been missing for a month!*\nBut don’t worry, your Korean lessons are waiting!";
    public static final List<String> THIRTY_DAY_MESSAGES = List.of(THIRTY_ONE_DAY_MESSAGE_1, THIRTY_ONE_DAY_MESSAGE_2);
}
