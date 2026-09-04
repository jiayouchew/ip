package wobble.gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import wobble.exceptions.WobbleException;
import wobble.parser.Parser;
import wobble.storage.Storage;
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

    /** Adds a bot response to the conversation using the response styling. */
    private void addBotMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.botMessage(message));
    }
}
