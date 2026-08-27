import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

/** Saves and loads Wobble tasks from a relative data file. */
public class Storage {
    private static final Path FILE_PATH = Path.of("data", "wobble.txt");

    /** Loads saved tasks, skipping malformed records. */
    public TaskList load() throws IOException {
        TaskList taskList = new TaskList();
        if (!Files.exists(FILE_PATH)) return taskList;
        for (String line : Files.readAllLines(FILE_PATH)) {
            if (line.isBlank()) continue;
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
        Files.createDirectories(FILE_PATH.getParent());
        StringBuilder contents = new StringBuilder();
        for (int i = 1; i <= taskList.size(); i++) {
            contents.append(serialize(taskList.get(i))).append(System.lineSeparator());
        }
        Files.writeString(FILE_PATH, contents.toString(), StandardCharsets.UTF_8);
    }

    private static String serialize(Task task) {
        String line = task.getType() + "|" + (task.isDone() ? "1" : "0")
                + "|" + encode(task.getDescription());
        if (task instanceof Deadline deadline) line += "|" + encode(deadline.getBy().toString());
        if (task instanceof Event event) {
            line += "|" + encode(event.getFrom().toString()) + "|" + encode(event.getTo().toString());
        }
        return line;
    }

    private static Task deserialize(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3 || !(fields[1].equals("0") || fields[1].equals("1"))) throw new IllegalArgumentException();
        Task task = switch (fields[0]) {
            case "TODO" -> fields.length == 3 ? new Todo(decode(fields[2])) : null;
            case "DEADLINE" -> fields.length == 4
                    ? new Deadline(decode(fields[2]), java.time.LocalDateTime.parse(decode(fields[3]))) : null;
            case "EVENT" -> fields.length == 5
                    ? new Event(decode(fields[2]), java.time.LocalDateTime.parse(decode(fields[3])),
                    java.time.LocalDateTime.parse(decode(fields[4]))) : null;
            default -> null;
        };
        if (task == null) throw new IllegalArgumentException();
        if (fields[1].equals("1")) task.markAsDone();
        return task;
    }

    private static String encode(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
