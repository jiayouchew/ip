package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task-list state changes and one-based user-facing indexing. */
class TaskListTest {
    @Test
    void add_taskIncreasesSizeAndPreservesTask() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(1));
    }

    @Test
    void get_invalidIndex_returnsNull() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertNull(taskList.get(0));
        assertNull(taskList.get(2));
    }

    @Test
    void delete_validIndexRemovesAndReturnsTask() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("submit report", java.time.LocalDateTime.of(2026, 8, 27, 0, 0)));

        Task removed = taskList.delete(1);

        assertEquals("read book", removed.getDescription());
        assertEquals(1, taskList.size());
        assertEquals("submit report", taskList.get(1).getDescription());
    }

    @Test
    void delete_invalidIndex_doesNotChangeList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertNull(taskList.delete(2));
        assertEquals(1, taskList.size());
        assertEquals("read book", taskList.get(1).getDescription());
    }

    @Test
    void findUpcoming_futureTasks_returnsUnfinishedTasksInTimeOrder() {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("later", LocalDateTime.of(2026, 9, 15, 9, 0)));
        Deadline completed = new Deadline("completed", LocalDateTime.of(2026, 9, 10, 9, 0));
        completed.markAsDone();
        taskList.add(completed);
        taskList.add(new Event("soon", LocalDateTime.of(2026, 9, 10, 8, 0),
                LocalDateTime.of(2026, 9, 10, 9, 0)));

        assertEquals(java.util.List.of(3, 1),
                taskList.findUpcoming(LocalDateTime.of(2026, 9, 10, 0, 0), 7));
    }

    @Test
    void findUpcoming_outsideWindow_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Deadline("later", LocalDateTime.of(2026, 9, 20, 9, 0)));

        assertEquals(java.util.List.of(),
                taskList.findUpcoming(LocalDateTime.of(2026, 9, 10, 0, 0), 7));
    }
}
