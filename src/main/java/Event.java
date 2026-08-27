/** A task that takes place between a stated start and end time. */
public class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description, TaskType.EVENT);
        this.from = from;
        this.to = to;
    }

    /** Returns the event start text. */
    public String getFrom() { return from; }

    /** Returns the event end text. */
    public String getTo() { return to; }

    @Override
    public String toString() {
        return super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
