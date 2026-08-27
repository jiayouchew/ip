package wobble.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.TaskList;
import wobble.tasks.Todo;

/** Tests storage creation, persistence, and restoration of task state. */
class StorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void load_missingFile_returnsEmptyTaskList() throws Exception {
        Storage storage = new Storage(temporaryDirectory.resolve("missing/wobble.txt"));
        assertEquals(0, storage.load().size());
    }

    @Test
    void saveAndLoad_allTaskTypesPreservesTasksAndStatus() throws Exception {
        Path file = temporaryDirectory.resolve("nested/wobble.txt");
        Storage storage = new Storage(file);
        TaskList original = new TaskList();
        original.add(new Todo("read | book"));
        Deadline deadline = new Deadline("submit report", LocalDateTime.of(2026, 8, 27, 18, 0));
        deadline.markAsDone();
        original.add(deadline);
        original.add(new Event("meeting", LocalDateTime.of(2026, 8, 27, 14, 0),
                LocalDateTime.of(2026, 8, 27, 16, 0)));

        storage.save(original);
        TaskList loaded = storage.load();

        assertEquals(3, loaded.size());
        assertEquals("[T][ ] read | book", loaded.get(1).toString());
        assertEquals("[D][X] submit report (by: Aug 27 2026 6:00 pm)", loaded.get(2).toString());
        assertEquals("[E][ ] meeting (from: Aug 27 2026 2:00 pm to: Aug 27 2026 4:00 pm)", loaded.get(3).toString());
        assertFalse(Files.readString(file).isBlank());
    }
}
