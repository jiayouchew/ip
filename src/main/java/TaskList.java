/** Stores the tasks created during one Wobble session. */
public class TaskList {
    private static final int MAX_TASKS = 100;
    private final Task[] tasks = new Task[MAX_TASKS];
    private int taskCount;

    /** Adds a new unfinished task and returns whether there was space for it. */
    public boolean add(String description) {
        if (taskCount >= MAX_TASKS) {
            return false;
        }
        tasks[taskCount] = new Task(description);
        taskCount++;
        return true;
    }

    /** Returns the number of stored tasks. */
    public int size() {
        return taskCount;
    }

    /** Returns the task at a one-based position, or null if the position is invalid. */
    public Task get(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > taskCount) {
            return null;
        }
        return tasks[oneBasedIndex - 1];
    }

    /** Returns the tasks currently stored in this list. */
    public Task[] getTasks() {
        return tasks;
    }
}
