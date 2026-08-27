/** A task that must be completed by a stated date or time. */
public class Deadline extends Task {
    private final String by;

    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    /** Returns the deadline text. */
    public String getBy() { return by; }

    @Override
    public String toString() {
        return super.toString() + " (by: " + by + ")";
    }
}
