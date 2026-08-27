package wobble.tasks;

/** A task without a date or time attached to it. */
public class Todo extends Task {
    public Todo(String description) {
        super(description, TaskType.TODO);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
