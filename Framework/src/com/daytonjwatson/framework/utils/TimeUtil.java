package com.daytonjwatson.framework.utils;

import java.time.Duration;
import java.util.Locale;

public class TimeUtil {
    public static String formatDuration(long millis) {
        Duration duration = Duration.ofMillis(millis);
        long days = duration.toDays();
        long hours = duration.minusDays(days).toHours();
        long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
        long seconds = duration.minusDays(days).minusHours(hours).minusMinutes(minutes).getSeconds();
        StringBuilder builder = new StringBuilder();
        if (days > 0) builder.append(days).append("d ");
        if (hours > 0) builder.append(hours).append("h ");
        if (minutes > 0) builder.append(minutes).append("m ");
        builder.append(seconds).append("s");
        return builder.toString().trim();
    }

    public static long parseDuration(String input) {
        long totalMillis = 0L;
        String number = "";
        for (char c : input.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isDigit(c)) {
                number += c;
            } else {
                long value = Long.parseLong(number.isEmpty() ? "0" : number);
                switch (c) {
                    case 'd' -> totalMillis += value * 86400000L;
                    case 'h' -> totalMillis += value * 3600000L;
                    case 'm' -> totalMillis += value * 60000L;
                    case 's' -> totalMillis += value * 1000L;
                }
                number = "";
            }
        }
        if (!number.isEmpty()) {
            totalMillis += Long.parseLong(number) * 1000L;
        }
        return totalMillis;
    }
}
