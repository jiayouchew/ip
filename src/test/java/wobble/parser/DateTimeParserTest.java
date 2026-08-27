package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/** Tests supported date/time inputs and user-friendly output formatting. */
class DateTimeParserTest {
    @Test
    void parse_isoDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0),
                DateTimeParser.parse("2026-08-27"));
    }

    @Test
    void parse_dottedDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0),
                DateTimeParser.parse("2026.08.27"));
    }

    @Test
    void parse_slashedDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0),
                DateTimeParser.parse("2026/08/27"));
    }

    @Test
    void parse_threeDigitYearDottedDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(999, 8, 27, 0, 0),
                DateTimeParser.parse("999.08.27"));
    }

    @Test
    void parse_threeDigitYearSlashedDate_returnsStartOfDay() {
        assertEquals(LocalDateTime.of(999, 8, 27, 0, 0),
                DateTimeParser.parse("999/08/27"));
    }

    @Test
    void parse_dateWithSurroundingWhitespace_trimsInput() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0),
                DateTimeParser.parse("  2026-08-27  "));
    }

    @Test
    void parse_compactTime_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 0),
                DateTimeParser.parse("2026-08-27 1800"));
    }

    @Test
    void parse_colonSeparatedTime_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30),
                DateTimeParser.parse("2026-08-27 18:30"));
    }

    @Test
    void parse_isoDateTime_returnsExpectedDateTime() {
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 30),
                DateTimeParser.parse("2026-08-27T18:30"));
    }

    @Test
    void parse_invalidDate_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2026-02-30"));
    }

    @Test
    void parse_invalidTime_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("2026-08-27 2500"));
    }

    @Test
    void parse_unsupportedFormat_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("27-08-2026"));
    }

    @Test
    void parse_blankInput_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class,
                () -> DateTimeParser.parse("   "));
    }

    @Test
    void format_dateOnly_returnsFriendlyDate() {
        assertEquals("Aug 27 2026", DateTimeParser.format(LocalDateTime.of(2026, 8, 27, 0, 0)));
    }

    @Test
    void format_dateAndTime_returnsFriendlyDateAndTime() {
        assertEquals("Aug 27 2026 6:30 pm",
                DateTimeParser.format(LocalDateTime.of(2026, 8, 27, 18, 30)));
    }

    @Test
    void format_midnightDateTime_returnsDateWithoutTime() {
        assertEquals("Jan 1 2026", DateTimeParser.format(LocalDateTime.of(2026, 1, 1, 0, 0)));
    }

    @Test
    void format_noonDateTime_uses12HourClock() {
        assertEquals("Aug 27 2026 12:00 pm",
                DateTimeParser.format(LocalDateTime.of(2026, 8, 27, 12, 0)));
    }
}
