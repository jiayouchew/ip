package wobble.tasks;

import wobble.parser.DateTimeParser;
import java.time.LocalDateTime;

/** A task that must be completed by a stated date or time. */
public class Deadline extends Task {
    private final LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the deadline text. */
    public LocalDateTime getBy() { return by; }

    @Override
    public String toString() {
        return super.toString() + " (by: " + DateTimeParser.format(by) + ")";
    }
}
