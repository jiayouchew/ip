package wobble;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

import wobble.exceptions.WobbleException;
import wobble.parser.Parser;
import wobble.storage.Storage;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.TaskList;
import wobble.ui.Ui;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    private final Ui ui = new Ui();
    private final Storage storage = new Storage();
    private final Parser parser = new Parser();

    /** Starts the chatbot and processes commands until the user exits. */
    public static void main(String[] args) {
        new Wobble().run();
    }

    /** Runs the command loop until the user exits or input ends. */
    private void run() {
        ui.showWelcome();
        TaskList taskList = loadTasks();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = ui.readCommand(scanner);

            if (processCommand(command, taskList)) {
                break;
            }
        }
        scanner.close();
    }

    /** Loads saved tasks or starts with an empty list when loading fails. */
    private TaskList loadTasks() {
        try {
            return storage.load();
        } catch (IOException exception) {
            System.out.println("Wobble diagnostic: saved tasks could not be loaded; starting with an empty tray.");
            return new TaskList();
        }
    }

    /** Processes one command and returns whether the application should exit. */
    private boolean processCommand(String command, TaskList taskList) {
        if (command.equals("bye")) {
            ui.showGoodbye();
            return true;
        }
        try {
            if (command.equals("due on") || command.startsWith("due on ")) {
                handleDateCommand(command, taskList, parser);
            } else if (command.equals("find") || command.startsWith("find ")) {
                handleFindCommand(command, taskList, ui);
            } else if (command.equals("help")) {
                ui.showHelp();
            } else if (command.equals("reminders") || command.startsWith("reminders ")) {
                int days = parser.parseReminderDays(command);
                LocalDateTime now = LocalDateTime.now();
                ui.showReminders(taskList, taskList.findUpcoming(now, days), now, days);
            } else if (command.equals("list")) {
                ui.showTasks(taskList);
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                handleDeleteCommand(command, taskList);
                storage.save(taskList);
            } else if (command.equals("mark") || command.startsWith("mark ")
                    || command.equals("unmark") || command.startsWith("unmark ")) {
                handleStatusCommand(command, taskList);
                storage.save(taskList);
            } else {
                Task task = parser.parseTask(command);
                taskList.add(task);
                storage.save(taskList);
                ui.showTaskAdded(task, taskList.size());
            }
        } catch (WobbleException exception) {
            ui.showDiagnostic(exception.getMessage());
        } catch (IOException exception) {
            ui.showDiagnostic("changes could not be saved.");
        }
        return false;
    }

    /** Validates a find command and asks the UI to display matching tasks. */
    private static void handleFindCommand(String command, TaskList taskList, Ui ui) throws WobbleException {
        String keyword = command.length() > 4 ? command.substring(4).trim() : "";
        if (keyword.isEmpty()) {
            throw new WobbleException("please use find <keyword>, for example: find book");
        }
        ui.showMatchingTasks(taskList, keyword);
    }

    /** Displays deadlines and events that occur on a requested date. */
    private static void handleDateCommand(String command, TaskList taskList, Parser parser) throws WobbleException {
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
                System.out.println(i + "." + task);
                matches++;
            }
        }
        if (matches == 0) {
            System.out.println("No deadlines or events are wobbling on that date.");
        }
    }

    /** Deletes the task referred to by a delete command. */
    private static void handleDeleteCommand(String command, TaskList taskList) throws WobbleException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("please use delete <number>, for example: delete 2");
        }
        try {
            Task removedTask = taskList.delete(Integer.parseInt(parts[1]));
            if (removedTask == null) {
                throw new WobbleException("that task number is off my radar. Your task list is unchanged.");
            }
            System.out.println("Noted. I've removed this task:");
            System.out.println("  " + removedTask);
            System.out.println("Now you have " + taskList.size() + " tasks in the list.");
        } catch (NumberFormatException exception) {
            throw new WobbleException("task numbers must be numbers, for example: delete 2");
        }
    }

    /** Marks or unmarks the task referred to by a status command. */
    private static void handleStatusCommand(String command, TaskList taskList) throws WobbleException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length != 2) {
            throw new WobbleException("please use mark <number> or unmark <number>, for example: mark 2");
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            Task task = taskList.get(taskNumber);
            if (task == null) {
                throw new WobbleException("that task number is off my radar. Your task list is unchanged.");
            }

            boolean markingDone = parts[0].equals("mark");
            if (markingDone) {
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
            }
            System.out.println("  " + task);
        } catch (NumberFormatException exception) {
            throw new WobbleException("task numbers must be numbers, for example: mark 2");
        }
    }
}
