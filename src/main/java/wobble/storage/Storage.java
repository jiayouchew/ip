package wobble.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;
import wobble.tasks.Todo;

/** Saves and loads Wobble tasks from a relative data file. */
public class Storage {
    private static final Path DEFAULT_FILE_PATH = Path.of("data", "wobble.txt");
    private final Path filePath;

    /** Creates storage using Wobble's default relative data file. */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /** Creates storage using a caller-provided path, useful for isolated tests. */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /** Loads saved tasks, skipping malformed records. */
    public TaskList load() throws IOException {
        TaskList taskList = new TaskList();
        if (!Files.exists(filePath)) {
            return taskList;
        }
        for (String line : Files.readAllLines(filePath)) {
            if (line.isBlank()) {
                continue;
            }
            try {
                taskList.add(deserialize(line));
            } catch (IllegalArgumentException exception) {
                System.out.println("Wobble diagnostic: skipped a corrupted saved task.");
            }
        }
        return taskList;
    }

    /** Saves all tasks, creating the data folder if necessary. */
    public void save(TaskList taskList) throws IOException {
        if (filePath.getParent() != null) {
            Files.createDirectories(filePath.getParent());
        }
        StringBuilder contents = new StringBuilder();
        for (int i = 1; i <= taskList.size(); i++) {
            contents.append(serialize(taskList.get(i))).append(System.lineSeparator());
        }
        Files.writeString(filePath, contents.toString(), StandardCharsets.UTF_8);
    }

    /** Converts one task into the pipe-delimited persistence format. */
    private static String serialize(Task task) {
        String line = task.getType() + "|" + (task.isDone() ? "1" : "0")
                + "|" + encode(task.getDescription());
        if (task instanceof Deadline deadline) {
            line += "|" + encode(deadline.getBy().toString());
        }
        if (task instanceof Event event) {
            line += "|" + encode(event.getFrom().toString()) + "|" + encode(event.getTo().toString());
        }
        return line;
    }

    /** Reconstructs one task from a persisted record. */
    private static Task deserialize(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) {
            throw new IllegalArgumentException();
        }
        Task task = switch (fields[0]) {
            case "TODO" -> fields.length == 3 ? new Todo(decode(fields[2])) : null;
            case "DEADLINE" -> fields.length == 4
                    ? new Deadline(decode(fields[2]), java.time.LocalDateTime.parse(decode(fields[3]))) : null;
            case "EVENT" -> fields.length == 5
                    ? new Event(decode(fields[2]), java.time.LocalDateTime.parse(decode(fields[3])),
                    java.time.LocalDateTime.parse(decode(fields[4]))) : null;
            default -> null;
        };
        if (task == null) {
            throw new IllegalArgumentException();
        }
        if (fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /** Encodes text so delimiters and special characters are safe in a record. */
    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    /** Decodes text previously written by {@link #encode(String)}. */
    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
