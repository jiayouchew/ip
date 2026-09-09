package wobble.tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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

    /** Returns unfinished deadlines and events occurring within a day range. */
    public List<Integer> findUpcoming(LocalDateTime now, int days) {
        assert now != null : "The reminder search must have a reference time";
        assert days >= 0 : "The reminder range must not be negative";
        LocalDateTime end = now.plusDays(days);
        return IntStream.range(0, tasks.size())
                .filter(index -> !tasks.get(index).isDone())
                .filter(index -> getScheduledTime(tasks.get(index)) != null)
                .filter(index -> !getScheduledTime(tasks.get(index)).isBefore(now)
                        && !getScheduledTime(tasks.get(index)).isAfter(end))
                .boxed()
                .sorted(Comparator.comparing(index -> getScheduledTime(tasks.get(index))))
                .map(index -> index + 1)
                .toList();
    }

    /** Returns the relevant due or start time for a scheduled task. */
    private static LocalDateTime getScheduledTime(Task task) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy();
        }
        if (task instanceof Event event) {
            return event.getFrom();
        }
        return null;
    }
}
