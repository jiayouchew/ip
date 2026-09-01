package wobble.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests task state changes and common task behavior. */
class TaskTest {
    @Test
    void newTask_isNotDoneAndHasDescription() {
        Task task = new Task("read book");
        assertFalse(task.isDone());
        assertEquals("read book", task.getDescription());
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void markAsDone_changesStatusToDone() {
        Task task = new Task("read book");
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    void markAsNotDone_changesDoneTaskBackToNotDone() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();
        assertFalse(task.isDone());
        assertEquals(" ", task.getStatusIcon());
    }
}
