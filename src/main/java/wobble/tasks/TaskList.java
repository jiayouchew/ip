package wobble.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

/** Stores the tasks created during one Wobble session. */
public class TaskList {
    private final List<Task> tasks = new ArrayList<>();

    /** Adds a task to the list. */
    public void add(Task task) {
        assert task != null : "The task list must not contain null tasks";
        tasks.add(task);
    }

    /** Returns the number of stored tasks. */
    public int size() {
        return tasks.size();
    }

    /** Returns the task at a one-based position, or null if the position is invalid. */
    public Task get(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            return null;
        }
        return tasks.get(oneBasedIndex - 1);
    }

    /** Removes and returns the task at a one-based position, or null if invalid. */
    public Task delete(int oneBasedIndex) {
        if (oneBasedIndex < 1 || oneBasedIndex > tasks.size()) {
            return null;
        }
        return tasks.remove(oneBasedIndex - 1);
    }

    /** Returns the one-based numbers of tasks whose descriptions contain a keyword. */
    public List<Integer> find(String keyword) {
        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        return IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .map(index -> index + 1)
                .boxed()
                .toList();
    }
}
