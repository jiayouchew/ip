package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests subtype-specific task formatting and date accessors. */
class TaskSubtypeTest {
    @Test
    void todo_toString_includesTodoType() {
        assertEquals("[T][ ] tidy room", new Todo("tidy room").toString());
    }

    @Test
    void deadline_toString_includesFormattedDeadline() {
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 27, 18, 0));
        assertEquals("Aug 27 2026 6:00 pm", deadline.getBy().format(java.time.format.DateTimeFormatter.ofPattern("MMM d yyyy h:mm a")));
        assertEquals("[D][ ] submit report (by: Aug 27 2026 6:00 pm)", deadline.toString());
    }

    @Test
    void event_toString_includesFormattedRange() {
        Event event = new Event("meeting", LocalDateTime.of(2026, 8, 27, 14, 0),
                LocalDateTime.of(2026, 8, 27, 16, 0));
        assertEquals("[E][ ] meeting (from: Aug 27 2026 2:00 pm to: Aug 27 2026 4:00 pm)", event.toString());
    }
}
