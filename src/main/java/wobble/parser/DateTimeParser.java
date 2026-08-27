package wobble.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Parses and displays date/time text used by deadlines and events. */
public final class DateTimeParser {
    private static final DateTimeFormatter[] INPUT_FORMATS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy.MM.dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("yyy.MM.dd"),
        DateTimeFormatter.ofPattern("yyy/MM/dd"),
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    };
    private static final DateTimeFormatter DATE_OUTPUT = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final DateTimeFormatter TIME_OUTPUT = DateTimeFormatter.ofPattern("MMM d yyyy h:mm a");

    private DateTimeParser() {
    }

    /** Parses a date or date/time string into a LocalDateTime. */
    public static LocalDateTime parse(String value) {
        String text = value.trim();
        try {
            for (DateTimeFormatter format : new DateTimeFormatter[] {
                    DateTimeFormatter.ISO_LOCAL_DATE,
                    DateTimeFormatter.ofPattern("yyyy.MM.dd"),
                    DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                    DateTimeFormatter.ofPattern("yyy.MM.dd"),
                    DateTimeFormatter.ofPattern("yyy/MM/dd") }) {
                try {
                    return LocalDate.parse(text, format).atStartOfDay();
                } catch (DateTimeParseException ignoredFormat) {
                    // Try the next supported date format.
                }
            }
        } catch (DateTimeParseException ignored) {
            // Try date/time formats below.
        }
        for (DateTimeFormatter format : INPUT_FORMATS) {
            try {
                return LocalDateTime.parse(text, format);
            } catch (DateTimeParseException ignoredFormat) {
                // Try the next supported format.
            }
        }
        throw new DateTimeParseException("Unsupported date/time", text, 0);
    }

    /** Formats a date/time for friendly display. */
    public static String format(LocalDateTime value) {
        return value.toLocalTime().equals(LocalTime.MIDNIGHT)
                ? value.format(DATE_OUTPUT) : value.format(TIME_OUTPUT);
    }
}
