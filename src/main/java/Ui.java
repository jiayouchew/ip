import java.util.Scanner;

/** Handles all interaction between Wobble and the user. */
public class Ui {
    /** Displays Wobble's welcome message. */
    public void showWelcome() {
        System.out.println("==============================");
        System.out.println("  WOBBL-E // Systems Online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("My memory tray is polished and ready for tasks.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");
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
