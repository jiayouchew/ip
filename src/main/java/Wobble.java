import java.util.Scanner;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("  WOBBL-E // systems online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("My memory tray is polished and ready for tasks.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");

        TaskList taskList = new TaskList();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("Wobble powering down... beep!");
                System.out.println("==============================");
                break;
            }

            if (command.equals("list")) {
                System.out.println("Scanning my task tray... beep!");
                if (taskList.size() == 0) {
                    System.out.println("Nothing is wobbling on the tray yet.");
                }
                for (int i = 1; i <= taskList.size(); i++) {
                    Task task = taskList.get(i);
                    System.out.println(i + ".[" + task.getStatusIcon() + "] "
                            + task.getDescription());
                }
            } else if (command.startsWith("mark ") || command.startsWith("unmark ")) {
                handleStatusCommand(command, taskList);
            } else if (taskList.add(command)) {
                System.out.println("added: " + command);
                System.out.println("Wobble note: safely tucked into the memory tray!");
            } else {
                System.out.println("Wobble alert: my task tray is full! Beep boop!");
            }
        }
        scanner.close();
    }

    /** Marks or unmarks the task referred to by a status command. */
    private static void handleStatusCommand(String command, TaskList taskList) {
        String[] parts = command.split(" ");
        if (parts.length != 2) {
            System.out.println("Wobble needs a task number, for example: mark 2");
            return;
        }

        try {
            int taskNumber = Integer.parseInt(parts[1]);
            Task task = taskList.get(taskNumber);
            if (task == null) {
                System.out.println("Wobble cannot find that task number. Beep?");
                return;
            }

            boolean markingDone = parts[0].equals("mark");
            if (markingDone) {
                task.markAsDone();
                System.out.println("Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println("OK, I've marked this task as not done yet:");
            }
            System.out.println("  [" + task.getStatusIcon() + "] " + task.getDescription());
        } catch (NumberFormatException exception) {
            System.out.println("Wobble needs a number, for example: mark 2");
        }
    }
}
