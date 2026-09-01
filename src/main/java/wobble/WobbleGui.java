package wobble;

import java.io.IOException;
import java.time.LocalDate;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import wobble.exceptions.WobbleException;
import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;

/** Provides a simple graphical interface for Wobble. */
public class WobbleGui extends Application {
    private final TextArea conversation = new TextArea();
    private final Parser parser = new Parser();
    private final Storage storage = new Storage();
    private TaskList taskList;

    /** Creates and displays the Wobble window. */
    @Override
    public void start(Stage stage) {
        loadTasks();
        Label title = new Label("WOBBL-E // Systems Online");
        conversation.setEditable(false);
        conversation.setWrapText(true);
        conversation.setText("Hello! I'm Wobble.\n"
                + "Beep boop! Your friendly little robot companion is ready.\n"
                + "What can I do for you?\n"
                + "Loaded " + taskList.size() + " task(s) from my memory tray.\n");

        TextField commandInput = new TextField();
        commandInput.setPromptText("Type a command...");
        Button sendButton = new Button("Send");
        sendButton.setOnAction(event -> submitCommand(commandInput));
        commandInput.setOnAction(event -> submitCommand(commandInput));

        HBox inputRow = new HBox(8, commandInput, sendButton);
        inputRow.setPadding(new Insets(8));
        BorderPane root = new BorderPane();
        root.setTop(title);
        root.setCenter(conversation);
        root.setBottom(inputRow);
        BorderPane.setMargin(title, new Insets(8));

        stage.setTitle("Wobble");
        stage.setScene(new Scene(root, 520, 360));
        stage.show();
    }

    /** Adds a user command and a simple response to the conversation. */
    private void submitCommand(TextField commandInput) {
        String command = commandInput.getText().trim();
        if (command.isEmpty()) {
            return;
        }
        conversation.appendText("\n> " + command + "\n");
        try {
            executeCommand(command);
        } catch (WobbleException exception) {
            conversation.appendText("Wobble diagnostic: " + exception.getMessage() + "\n");
        } catch (IOException exception) {
            conversation.appendText("Wobble diagnostic: changes could not be saved.\n");
        }
        commandInput.clear();
    }

    /** Loads saved tasks when the GUI starts. */
    private void loadTasks() {
        try {
            taskList = storage.load();
        } catch (IOException exception) {
            taskList = new TaskList();
        }
    }

    /** Executes a command using the existing Wobble task model. */
    private void executeCommand(String command) throws WobbleException, IOException {
        if (command.equals("bye")) {
            conversation.appendText("Bye. Hope to see you again soon!\n");
        } else if (command.equals("list")) {
            showTaskList();
        } else if (command.equals("find") || command.startsWith("find ")) {
            showMatchingTasks(command);
        } else if (command.equals("due on") || command.startsWith("due on ")) {
            showTasksDueOn(command);
        } else if (command.equals("mark") || command.startsWith("mark ")
                || command.equals("unmark") || command.startsWith("unmark ")) {
            updateTaskStatus(command);
        } else if (command.equals("delete") || command.startsWith("delete ")) {
            deleteTask(command);
        } else {
            Task task = parser.parseTask(command);
            taskList.add(task);
            storage.save(taskList);
            conversation.appendText("Beep boop! Added: " + task + "\n");
        }
    }

    /** Displays all tasks currently stored by Wobble. */
    private void showTaskList() {
        conversation.appendText("Here are the tasks in your list:\n");
        if (taskList.size() == 0) {
            conversation.appendText("Nothing is wobbling on the tray yet.\n");
        }
        for (int i = 1; i <= taskList.size(); i++) {
            conversation.appendText(i + "." + taskList.get(i) + "\n");
        }
    }

    /** Displays tasks whose descriptions contain the requested keyword. */
    private void showMatchingTasks(String command) throws WobbleException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new WobbleException("please use find <keyword>");
        }
        conversation.appendText("Here are the matching tasks in your list:\n");
        for (int taskNumber : taskList.find(keyword)) {
            conversation.appendText(taskNumber + "." + taskList.get(taskNumber) + "\n");
        }
    }

    /** Displays deadlines and events occurring on the requested date. */
    private void showTasksDueOn(String command) throws WobbleException {
        LocalDate date = parser.parseDueDate(command);
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
                conversation.appendText(i + "." + task + "\n");
                matches++;
            }
        }
        if (matches == 0) {
            conversation.appendText("No deadlines or events are wobbling on that date.\n");
        }
    }

    /** Marks or unmarks a task and persists the updated status. */
    private void updateTaskStatus(String command) throws WobbleException, IOException {
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
        conversation.appendText("Updated: " + task + "\n");
    }

    /** Deletes a task and persists the updated task list. */
    private void deleteTask(String command) throws WobbleException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("please use delete <number>");
        }
        Task task = getTask(parts[1]);
        taskList.delete(Integer.parseInt(parts[1]));
        storage.save(taskList);
        conversation.appendText("Removed: " + task + "\n");
    }

    /** Returns a task selected by its one-based number. */
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
}
