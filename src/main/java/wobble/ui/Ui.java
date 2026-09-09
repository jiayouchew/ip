package wobble.ui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import wobble.parser.DateTimeParser;
import wobble.tasks.Task;
import wobble.tasks.TaskList;

/** Handles all interaction between Wobble and the user. */
public class Ui {
    /** Displays Wobble's welcome message. */
    public void showWelcome() {
        System.out.println("==============================");
        System.out.println("  WOBBLE // Systems Online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("My memory tray is polished and ready for tasks.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");
    }

    /** Displays the command formats supported by Wobble. */
    public void showHelp() {
        System.out.println("Wobble command guide");
        System.out.println();
        System.out.println("Add tasks:");
        System.out.println("  todo <description>");
        System.out.println("  deadline <description> /by <date/time>");
        System.out.println("  event <description> /from <date/time> /to <date/time>");
        System.out.println();
        System.out.println("Manage tasks:");
        System.out.println("  list");
        System.out.println("  find <keyword>");
        System.out.println("  mark <number>");
        System.out.println("  unmark <number>");
        System.out.println("  delete <number>");
        System.out.println();
        System.out.println("Dates and reminders:");
        System.out.println("  due on <date>");
        System.out.println("  reminders [number of days]");
        System.out.println("  Date: yyyy-MM-dd, yyyy.MM.dd, or yyyy/MM/dd");
        System.out.println("  Time: yyyy-MM-dd HHmm or yyyy-MM-dd HH:mm");
        System.out.println("  Example: deadline submit report /by 2026-09-15 1800");
        System.out.println();
        System.out.println("Exit:");
        System.out.println("  bye");
    }

    /** Reads the next command, or returns null when input ends. */
    public String readCommand(Scanner scanner) {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Displays a task list. */
    public void showTasks(TaskList taskList) {
        System.out.println("Scanning my task tray... whirr, beep!");
        if (taskList.size() == 0) {
            System.out.println("Nothing is wobbling on the tray yet. A very tidy tray!");
        }
        for (int i = 1; i <= taskList.size(); i++) {
            System.out.println(i + "." + taskList.get(i));
        }
    }

    /** Displays tasks matching a keyword and preserves their original list numbers. */
    public void showMatchingTasks(TaskList taskList, String keyword) {
        System.out.println("Here are the matching tasks in your list:");
        List<Integer> matchingTaskNumbers = taskList.find(keyword);
        if (matchingTaskNumbers.isEmpty()) {
            System.out.println("No tasks match that keyword. Wobble searched everywhere!");
        }
        for (int taskNumber : matchingTaskNumbers) {
            System.out.println(taskNumber + "." + taskList.get(taskNumber));
        }
    }

    /** Displays unfinished deadlines and events within the requested day range. */
    public void showReminders(TaskList taskList, List<Integer> taskNumbers,
            LocalDateTime now, int days) {
        if (taskNumbers.isEmpty()) {
            System.out.println("No upcoming reminders are wobbling in the next " + days + " days.");
            return;
        }
        System.out.println("Here are your upcoming reminders:");
        for (int taskNumber : taskNumbers) {
            System.out.println(taskNumber + "." + taskList.get(taskNumber));
        }
        System.out.println("Reminder window starts " + DateTimeParser.format(now) + ".");
    }

    /** Displays an error diagnostic. */
    public void showDiagnostic(String message) {
        System.out.println("Wobble diagnostic: " + message);
    }

    /** Displays the successful task-addition message. */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println("Beep boop! Got it. I've added this task to my memory tray:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println("Wobble powering down... beep!");
        System.out.println("==============================");
    }
}
