package wobble.tasks;

import java.time.LocalDateTime;

import wobble.parser.DateTimeParser;

/** A task that takes place between a stated start and end time. */
public class Event extends Task {
    private final LocalDateTime from;
    private final LocalDateTime to;

    /** Creates an unfinished event with its start and end date/time. */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description, TaskType.EVENT);
        assert from != null : "An event must have a start date";
        assert to != null : "An event must have an end date";
        assert !from.isAfter(to) : "An event must not end before it starts";
        this.from = from;
        this.to = to;
    }

    /** Returns the event start text. */
    public LocalDateTime getFrom() {
        return from;
    }

    /** Returns the event end text. */
    public LocalDateTime getTo() {
        return to;
    }

    /** Returns this event's display representation. */
    @Override
    public String toString() {
        return super.toString() + " (from: " + DateTimeParser.format(from)
                + " to: " + DateTimeParser.format(to) + ")";
    }
}
