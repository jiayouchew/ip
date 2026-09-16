package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import wobble.exceptions.WobbleException;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.Todo;

/** Tests the command parser's core task-creation and validation logic. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseTask_todoCommand_createsTodo() throws WobbleException {
        Task task = parser.parseTask("todo read book");

        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseTask_mixedCaseAndExtraWhitespace_normalizesCommand() throws WobbleException {
        Task task = parser.parseTask("  TODO   read   book  ");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseTask_deadlineCommand_createsDeadlineWithDate() throws WobbleException {
        Task task = parser.parseTask("deadline submit report /by 2026-08-27 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals(LocalDateTime.of(2026, 8, 27, 18, 0), deadline.getBy());
    }

    @Test
    void parseTask_eventCommand_createsEventWithDateRange() throws WobbleException {
        Task task = parser.parseTask("event meeting /from 2026/08/27 /to 2026.08.28");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2026, 8, 28, 0, 0), event.getTo());
    }

    @Test
    void parseTask_emptyTodoDescription_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseTask("todo"));
    }

    @Test
    void parseTask_unknownCommand_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseTask("blah"));
    }

    @Test
    void parseTask_malformedDeadline_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report"));

        assertEquals("a deadline needs a description and a /by date.", exception.getMessage());
    }

    @Test
    void parseTask_invalidDeadlineDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by not-a-date"));
    }

    @Test
    void parseTask_invalidDeadlineTime_showsSupportedTimeRange() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by 2026-08-27 24:00"));

        assertEquals("the deadline date or time is invalid. Try yyyy-MM-dd HH:mm, "
                        + "for example 2026-09-16 18:00.",
                exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_repeatedDeadlineParameter_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by 2026-08-27 /by 2026-08-28"));

        assertEquals("a deadline accepts only one /by date.", exception.getMessage());
    }

    @Test
    void parseTask_missingDeadlineDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by"));
    }

    @Test
    void parseTask_emptyDeadlineDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline /by 2026-08-27"));
    }

    @Test
    void parseTask_repeatedEventParameter_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27 /to 2026-08-28 /to 2026-08-29"));

        assertEquals("an event accepts only one /from time and one /to time.", exception.getMessage());
    }

    @Test
    void parseTask_nonIncreasingEventRange_throwsWobbleException() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-28 /to 2026-08-27"));

        assertEquals("an event must end after it starts.", exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_invalidEventTime_showsSupportedTimeRange() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseTask("event meeting /from 2026-08-27 2200 /to 2026-08-27 24:00"));

        assertEquals("the event date or time is invalid. Try yyyy-MM-dd HH:mm, "
                        + "for example 2026-09-16 18:00.",
                exception.getMessage());
        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseTask_emptyEventDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("event /from 2026-08-27 /to 2026-08-28"));
    }

    @Test
    void parseTask_controlCharacterInDescription_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("todo read\u0000book"));
    }

    @Test
    void parseDueDate_validDate_returnsDate() throws WobbleException {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0).toLocalDate(),
                parser.parseDueDate("due on 2026/08/27"));
    }

    @Test
    void parseDueDate_missingDate_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseDueDate("due on"));
    }

    @Test
    void parseDueDate_invalidDate_suppressesCommandSuggestion() {
        WobbleException exception = assertThrows(WobbleException.class,
                () -> parser.parseDueDate("due on 2027-02-31"));

        assertFalse(exception.shouldSuggestCommand());
    }

    @Test
    void parseDueDate_mixedCaseAndExtraWhitespace_returnsDate() throws WobbleException {
        assertEquals(LocalDateTime.of(2026, 8, 27, 0, 0).toLocalDate(),
                parser.parseDueDate("  DUE   ON   2026/08/27  "));
    }

    @Test
    void parseReminderDays_missingRange_usesSevenDays() throws WobbleException {
        assertEquals(7, parser.parseReminderDays("reminders"));
    }

    @Test
    void parseReminderDays_invalidRange_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> parser.parseReminderDays("reminders tomorrow"));
        assertThrows(WobbleException.class, () -> parser.parseReminderDays("reminders -1"));
    }

    @Test
    void parseReminderDays_oversizedRange_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseReminderDays("reminders " + Integer.MAX_VALUE));
    }

    @Test
    void parseTaskNumber_specialCharacters_throwsWobbleException() {
        assertThrows(WobbleException.class, () -> Parser.parseTaskNumber("+1"));
    }
}
