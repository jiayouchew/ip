package wobble.gui;

import java.io.IOException;
import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import wobble.exceptions.WobbleException;
import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;

/** Controls the main Wobble conversation window and task commands. */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private final Parser parser = new Parser();
    private final Storage storage = new Storage();
    private TaskList taskList;

    /** Initializes the conversation area and loads saved tasks. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        loadTasks();
        addBotMessage("Hello! I'm Wobble.\nBeep boop! My memory tray is ready.");
    }

    /** Processes the command entered by the user and displays Wobble's reply. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.userMessage(input));
        try {
            addBotMessage(execute(input));
        } catch (WobbleException exception) {
            addBotMessage("Wobble diagnostic: " + exception.getMessage());
        } catch (IOException exception) {
            addBotMessage("Wobble diagnostic: changes could not be saved.");
        }
        userInput.clear();
    }

    /** Loads saved tasks or starts with an empty list when no save file exists. */
    private void loadTasks() {
        try {
            taskList = storage.load();
        } catch (IOException exception) {
            taskList = new TaskList();
        }
    }

    /** Executes a command using Wobble's existing task logic. */
    private String execute(String command) throws WobbleException, IOException {
        if (command.equals("bye")) {
            return "Bye. Hope to see you again soon!";
        }
        if (command.equals("list")) {
            return listTasks();
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return findTasks(command);
        }
        if (command.equals("due on") || command.startsWith("due on ")) {
            return tasksDueOn(command);
        }
        if (command.equals("mark") || command.startsWith("mark ")
                || command.equals("unmark") || command.startsWith("unmark ")) {
            return updateTaskStatus(command);
        }
        if (command.equals("delete") || command.startsWith("delete ")) {
            return deleteTask(command);
        }
        Task task = parser.parseTask(command);
        taskList.add(task);
        storage.save(taskList);
        return "Beep boop! Added to my memory tray:\n  " + task;
    }

    /** Returns a formatted representation of the current task list. */
    private String listTasks() {
        StringBuilder result = new StringBuilder("Here are the tasks in your list:");
        for (int i = 1; i <= taskList.size(); i++) {
            result.append("\n").append(i).append(".").append(taskList.get(i));
        }
        if (taskList.size() == 0) {
            result.append("\nNothing is wobbling on the tray yet.");
        }
        return result.toString();
    }

    /** Returns tasks whose descriptions contain the requested keyword. */
    private String findTasks(String command) throws WobbleException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new WobbleException("please use find <keyword>");
        }
        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:");
        for (int taskNumber : taskList.find(keyword)) {
            result.append("\n").append(taskNumber).append(".").append(taskList.get(taskNumber));
        }
        return result.toString();
    }

    /** Returns deadlines and events occurring on the requested date. */
    private String tasksDueOn(String command) throws WobbleException {
        LocalDate date = parser.parseDueDate(command);
        StringBuilder result = new StringBuilder("Tasks due on " + date + ":");
        int matches = 0;
        for (int i = 1; i <= taskList.size(); i++) {
            Task task = taskList.get(i);
            boolean occursOnDate = task instanceof Deadline deadline
                    && deadline.getBy().toLocalDate().equals(date);
            if (task instanceof Event event) {
                occursOnDate = !date.isBefore(event.getFrom().toLocalDate())
                        && !date.isAfter(event.getTo().toLocalDate());
            }
            if (occursOnDate) {
                result.append("\n").append(i).append(".").append(task);
                matches++;
            }
        }
        if (matches == 0) {
            result.append("\nNo deadlines or events are wobbling on that date.");
        }
        return result.toString();
    }

    /** Marks or unmarks a task and saves the updated status. */
    private String updateTaskStatus(String command) throws WobbleException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("please use mark <number> or unmark <number>");
        }
        Task task = getTask(parts[1]);
        if (parts[0].equals("mark")) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(taskList);
        return "Updated: " + task;
    }

    /** Deletes a task and saves the updated task list. */
    private String deleteTask(String command) throws WobbleException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("please use delete <number>");
        }
        Task task = getTask(parts[1]);
        taskList.delete(Integer.parseInt(parts[1]));
        storage.save(taskList);
        return "Removed: " + task;
    }

    /** Returns the task selected by a one-based number. */
    private Task getTask(String taskNumber) throws WobbleException {
        try {
            Task task = taskList.get(Integer.parseInt(taskNumber));
            if (task == null) {
                throw new WobbleException("that task number is off my radar.");
            }
            return task;
        } catch (NumberFormatException exception) {
            throw new WobbleException("task numbers must be numbers.");
        }
    }

    /** Adds a bot response to the conversation using the response styling. */
    private void addBotMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.botMessage(message));
    }
}
