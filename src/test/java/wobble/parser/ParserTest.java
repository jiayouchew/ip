package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report"));
    }

    @Test
    void parseTask_invalidDeadlineDate_throwsWobbleException() {
        assertThrows(WobbleException.class,
                () -> parser.parseTask("deadline submit report /by not-a-date"));
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
}
