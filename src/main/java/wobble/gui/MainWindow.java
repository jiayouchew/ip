package wobble.gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import wobble.exceptions.WobbleException;
import wobble.parser.CommandSuggester;
import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;

/** Controls the main Wobble conversation window and task commands. */
public class MainWindow extends BorderPane {
    private static final int SHUTDOWN_DOT_COUNT = 10;
    private static final double SHUTDOWN_DOT_INTERVAL_MILLIS = 80;
    private static final double SHUTDOWN_FINISH_DELAY_MILLIS = 500;
    private static final String BACKGROUND_IMAGE_PATH = "/images/wobble-background.png";
    private static final String ONLINE_SUBTITLE = "TASK COMPANION // MEMORY TRAY ONLINE";
    private static final String OFFLINE_SUBTITLE = "TASK COMPANION // MEMORY TRAY OFFLINE";
    private static final String SHUTDOWN_INTRO = "Bye, human! Wobble is signing off.\n"
            + "SHUTDOWN_SEQUENCE :: START\n";
    private static final String[] SHUTDOWN_OPERATIONS = {
        "save memory",
        "lock task tray",
        "disconnect"
    };

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Label statusLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private VBox appHeader;
    @FXML
    private StackPane conversationArea;

    private final Parser parser = new Parser();
    private final Storage storage = new Storage();
    private TaskList taskList;
    private boolean isClosing;

    /** Initializes the conversation area and loads saved tasks. */
    @FXML
    public void initialize() {
        configureConversationBackground();
        scrollPane.setPannable(true);
        subtitleLabel.setText(ONLINE_SUBTITLE);
        loadTasks();
        addBotMessage("Hello! I'm Wobble.\nBeep boop! My memory tray is ready.\n"
                + "Memory tray calibrated. Awaiting your next mission.");
        scrollToBottom();
    }

    /** Processes the command entered by the user and displays Wobble's reply. */
    @FXML
    private void handleUserInput() {
        if (isClosing) {
            return;
        }
        String input = userInput.getText().trim();
        if (input.isEmpty()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.userMessage(input));
        try {
            String response = execute(input);
            if (input.equals("bye")) {
                DialogBox shutdownMessage = addShutdownMessage(response);
                setOfflineAndExit(shutdownMessage);
            } else if (input.equals("help")) {
                addHelpMessage(response);
            } else {
                addBotMessage(response);
            }
        } catch (WobbleException exception) {
            String diagnostic = "Wobble diagnostic: " + exception.getMessage();
            if (exception.shouldSuggestCommand()) {
                diagnostic += "\nDo you mean " + CommandSuggester.suggest(input) + "?";
            }
            addErrorMessage(diagnostic);
        } catch (IOException exception) {
            addErrorMessage("Wobble diagnostic: changes could not be saved.");
        } finally {
            userInput.clear();
            scrollToBottom();
        }
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
            return SHUTDOWN_INTRO;
        }
        if (command.equals("list")) {
            return listTasks();
        }
        if (command.equals("help")) {
            return helpText();
        }
        if (command.equals("find") || command.startsWith("find ")) {
            return findTasks(command);
        }
        if (command.equals("due on") || command.startsWith("due on ")) {
            return tasksDueOn(command);
        }
        if (command.equals("reminders") || command.startsWith("reminders ")) {
            return reminders(command);
        }
        if (command.equals("mark") || command.startsWith("mark ")
                || command.equals("unmark") || command.startsWith("unmark ")) {
            return updateTaskStatus(command);
        }
        if (command.equals("delete") || command.startsWith("delete ")
                || command.equals("remove") || command.startsWith("remove ")) {
            return deleteTask(command);
        }
        Task task = parser.parseTask(command);
        taskList.add(task);
        storage.save(taskList);
        return "Beep boop! Task docked in my memory tray:\n  " + task
                + "\nI'll keep an eye on it.";
    }

    /** Returns a formatted representation of the current task list. */
    private String listTasks() {
        StringBuilder result = new StringBuilder("Memory tray scan complete.\nHere are the tasks in your list:");
        for (int i = 1; i <= taskList.size(); i++) {
            result.append("\n").append(i).append(".").append(taskList.get(i));
        }
        if (taskList.size() == 0) {
            result.append("\nNothing is wobbling on the tray yet.");
        }
        return result.toString();
    }

    /** Returns a single-card, readable guide to Wobble's commands and input formats. */
    private String helpText() {
        return "WOBBLE COMMAND DECK // QUICK REFERENCE\n"
                + "Pick a command below. Type help again whenever you need a systems check.\n\n"
                + "ADD TASKS\n"
                + "Save a task without a date:\n"
                + "  todo <description>\n\n"
                + "Save a task with a due date or time:\n"
                + "  deadline <description> /by <date/time>\n\n"
                + "Save a task that spans a time range:\n"
                + "  event <description> /from <start> /to <end>\n\n"
                + "MANAGE TASKS\n"
                + "Show every task:\n"
                + "  list\n\n"
                + "Search task descriptions:\n"
                + "  find <keyword>\n\n"
                + "Mark a task as done:\n"
                + "  mark <number>\n\n"
                + "Mark a task as not done:\n"
                + "  unmark <number>\n\n"
                + "Remove a task:\n"
                + "  delete <number>\n"
                + "  remove <number> (alias)\n\n"
                + "DATES AND REMINDERS\n"
                + "Show deadlines and events on a date:\n"
                + "  due on <date>\n\n"
                + "Show upcoming tasks for 7 days:\n"
                + "  reminders\n\n"
                + "Choose a reminder window:\n"
                + "  reminders <days>\n\n"
                + "DATE FORMATS\n"
                + "Date-only format (examples):\n"
                + "  yyyy-MM-dd    2026-09-15\n"
                + "  yyyy.MM.dd    2026.09.15\n"
                + "  yyyy/MM/dd    2026/09/15\n"
                + "  yyy.MM.dd or yyy/MM/dd\n\n"
                + "Date + time uses any date format above, followed by:\n"
                + "  <date> HHmm       2026/09/15 1800\n"
                + "  <date> HH:mm      2026.09.15 18:00\n\n"
                + "EXAMPLE\n"
                + "Add a report deadline:\n"
                + "  deadline submit report /by 2026-09-15 1800\n\n"
                + "EXIT\n"
                + "Power down Wobble:\n"
                + "  bye";
    }

    /** Returns tasks whose descriptions contain the requested keyword. */
    private String findTasks(String command) throws WobbleException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new WobbleException("a search keyword is required.");
        }
        StringBuilder result = new StringBuilder("Signal scan complete.\nHere are the matching tasks in your list:");
        for (int taskNumber : taskList.find(keyword)) {
            result.append("\n").append(taskNumber).append(".").append(taskList.get(taskNumber));
        }
        return result.toString();
    }

    /** Returns deadlines and events occurring on the requested date. */
    private String tasksDueOn(String command) throws WobbleException {
        LocalDate date = parser.parseDueDate(command);
        StringBuilder result = new StringBuilder("Time scanner locked onto " + date + ":");
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

    /** Returns unfinished deadlines and events within the requested day range. */
    private String reminders(String command) throws WobbleException {
        int days = parser.parseReminderDays(command);
        LocalDateTime now = LocalDateTime.now();
        List<Integer> upcomingTaskNumbers = taskList.findUpcoming(now, days);
        StringBuilder result = new StringBuilder("Radar sweep complete. Here are your upcoming reminders:");
        for (int taskNumber : upcomingTaskNumbers) {
            result.append("\n").append(taskNumber).append(".").append(taskList.get(taskNumber));
        }
        if (upcomingTaskNumbers.isEmpty()) {
            result.append("\nRadar clear. No upcoming reminders are wobbling in the next ")
                    .append(days).append(" days.");
        }
        return result.toString();
    }

    /** Marks or unmarks a task and saves the updated status. */
    private String updateTaskStatus(String command) throws WobbleException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("a task number is required to mark or unmark a task.");
        }
        Task task = getTask(parts[1]);
        if (parts[0].equals("mark")) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(taskList);
        String status = parts[0].equals("mark") ? "marked as done" : "marked as not done";
        return "Status sync complete. Task " + status + ":\n  " + task;
    }

    /** Deletes a task and saves the updated task list. */
    private String deleteTask(String command) throws WobbleException, IOException {
        String[] parts = command.split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("a task number is required.");
        }
        Task task = getTask(parts[1]);
        taskList.delete(Integer.parseInt(parts[1]));
        storage.save(taskList);
        return "Memory tray update: removed:\n  " + task
                + "\nThe tray now holds " + taskList.size() + " tasks.";
    }

    /** Returns the task selected by a one-based number. */
    private Task getTask(String taskNumber) throws WobbleException {
        try {
            Task task = taskList.get(Integer.parseInt(taskNumber));
            if (task == null) {
                throw new WobbleException(invalidTaskNumberMessage(), false);
            }
            return task;
        } catch (NumberFormatException exception) {
            throw new WobbleException("task numbers must be numbers.");
        }
    }

    /** Returns a clear explanation of the valid task-number range. */
    private String invalidTaskNumberMessage() {
        if (taskList.size() == 0) {
            return "There are no tasks in the list yet.";
        }
        return "That task number is off my radar. Choose a number from 1 to "
                + taskList.size() + ".";
    }

    /** Adds a bot response to the conversation using the response styling. */
    private void addBotMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.botMessage(message));
    }

    /** Adds the help guide as one structured card with readable internal headings. */
    private void addHelpMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.helpMessage(message));
    }

    /** Adds an error response with a visual treatment distinct from normal replies. */
    private void addErrorMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.errorMessage(message));
    }

    /** Adds Wobble's final robot-console message before the application exits. */
    private DialogBox addShutdownMessage(String message) {
        DialogBox shutdownMessage = DialogBox.shutdownMessage(message);
        dialogContainer.getChildren().add(shutdownMessage);
        return shutdownMessage;
    }

    /** Scrolls to the newest message after JavaFX has completed the updated conversation layout. */
    private void scrollToBottom() {
        Platform.runLater(() -> {
            dialogContainer.applyCss();
            dialogContainer.layout();
            scrollPane.layout();
            Platform.runLater(() -> scrollPane.setVvalue(1.0));
        });
    }

    /** Updates the shutdown card and keeps the newest animation line visible. */
    private void updateShutdownCard(DialogBox shutdownMessage, String text) {
        shutdownMessage.updateShutdownMessage(text);
        scrollToBottom();
    }

    /** Applies the robot-maintenance background without distorting it during window resizing. */
    private void configureConversationBackground() {
        URL imageUrl = MainWindow.class.getResource(BACKGROUND_IMAGE_PATH);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing Wobble background resource.");
        }
        Image background = new Image(imageUrl.toExternalForm());
        BackgroundSize coverSize = new BackgroundSize(100, 100, true, true, false, true);
        BackgroundImage backgroundImage = new BackgroundImage(background,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                coverSize);
        conversationArea.setBackground(new Background(backgroundImage));
    }

    /** Switches Wobble offline and starts its animated robot-console shutdown sequence. */
    private void setOfflineAndExit(DialogBox shutdownMessage) {
        isClosing = true;
        statusLabel.setText("● OFFLINE");
        subtitleLabel.setText(OFFLINE_SUBTITLE);
        statusLabel.getStyleClass().remove("app-status-online");
        statusLabel.getStyleClass().add("app-status-offline");
        appHeader.getStyleClass().add("offline-header");

        animateShutdownStep(shutdownMessage, new StringBuilder(SHUTDOWN_INTRO), 0);
    }

    /** Animates one shutdown operation by progressively adding dots before displaying {@code OK}. */
    private void animateShutdownStep(DialogBox shutdownMessage, StringBuilder output, int operationIndex) {
        if (operationIndex >= SHUTDOWN_OPERATIONS.length) {
            output.append("\nSTATUS :: OFFLINE // beep... boop.")
                    .append("\nSee you on the next boot, human.");
            updateShutdownCard(shutdownMessage, output.toString());
            PauseTransition shutdownDelay = new PauseTransition(
                    Duration.millis(SHUTDOWN_FINISH_DELAY_MILLIS));
            shutdownDelay.setOnFinished(event -> Platform.exit());
            shutdownDelay.play();
            return;
        }

        String operation = SHUTDOWN_OPERATIONS[operationIndex];
        Timeline progress = new Timeline();
        for (int dotCount = 1; dotCount <= SHUTDOWN_DOT_COUNT; dotCount++) {
            int currentDotCount = dotCount;
            progress.getKeyFrames().add(new KeyFrame(
                    Duration.millis(currentDotCount * SHUTDOWN_DOT_INTERVAL_MILLIS),
                    event -> updateShutdownCard(shutdownMessage,
                            formatShutdownProgress(output, operation, currentDotCount))));
        }
        progress.getKeyFrames().add(new KeyFrame(
                Duration.millis((SHUTDOWN_DOT_COUNT + 1) * SHUTDOWN_DOT_INTERVAL_MILLIS),
                event -> {
                    output.append("\n> ").append(operation).append(" ")
                            .append(".".repeat(SHUTDOWN_DOT_COUNT)).append(" OK");
                    updateShutdownCard(shutdownMessage, output.toString());
                    animateShutdownStep(shutdownMessage, output, operationIndex + 1);
                }));
        progress.play();
    }

    /** Builds the shutdown card text for an operation that is still reporting progress. */
    private static String formatShutdownProgress(StringBuilder completedOutput, String operation,
            int dotCount) {
        String progress = ".".repeat(dotCount);
        return completedOutput + "\n> " + operation + " " + progress;
    }
}
