package dev.awn.service.time;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class TimeService {
    public String unixToDate(String value, boolean millis) {
        long raw = Long.parseLong(value.trim());
        Instant instant = millis ? Instant.ofEpochMilli(raw) : Instant.ofEpochSecond(raw);
        return render(instant);
    }

    public String dateToUnix(String value) {
        Instant instant = parseInstant(value);
        return "Seconds: " + instant.getEpochSecond() + "\nMilliseconds: " + instant.toEpochMilli();
    }

    public String parseIso(String value) {
        return render(parseInstant(value));
    }

    public String duration(long value, String unit) {
        Duration duration = switch (unit) {
            case "seconds" -> Duration.ofSeconds(value);
            case "minutes" -> Duration.ofMinutes(value);
            case "hours" -> Duration.ofHours(value);
            case "days" -> Duration.ofDays(value);
            default -> Duration.ofMillis(value);
        };
        return "Milliseconds: " + duration.toMillis()
                + "\nSeconds: " + duration.toSeconds()
                + "\nMinutes: " + duration.toMinutes()
                + "\nHours: " + duration.toHours()
                + "\nDays: " + duration.toDays();
    }

    private Instant parseInstant(String value) {
        String text = value.trim();
        try {
            return Instant.parse(text);
        } catch (Exception ignored) {
            return LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.systemDefault()).toInstant();
        }
    }

    private String render(Instant instant) {
        return "UTC: " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(ZoneOffset.UTC))
                + "\nLocal: " + DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(instant.atZone(ZoneId.systemDefault()))
                + "\nUnix seconds: " + instant.getEpochSecond()
                + "\nUnix milliseconds: " + instant.toEpochMilli();
    }
}
