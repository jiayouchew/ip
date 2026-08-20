/** Represents one task and whether it has been completed. */
public class Task {
    private final String description;
    private boolean isDone;

    /** Creates an unfinished task with the given description. */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        isDone = true;
    }

    /** Marks this task as unfinished. */
    public void markAsNotDone() {
        isDone = false;
    }

    /** Returns the status icon used when displaying this task. */
    public String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    /** Returns the task description. */
    public String getDescription() {
        return description;
    }

    /** Returns the common status portion of a task's display text. */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
