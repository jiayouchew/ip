import java.util.Scanner;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("  WOBBL-E // Systems Online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("My memory tray is polished and ready for tasks.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");

        Storage storage = new Storage();
        TaskList taskList;
        try {
            taskList = storage.load();
        } catch (java.io.IOException exception) {
            taskList = new TaskList();
            System.out.println("Wobble diagnostic: saved tasks could not be loaded; starting with an empty tray.");
        }
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
                System.out.println("Scanning my task tray... whirr, beep!");
                if (taskList.size() == 0) {
                    System.out.println("Nothing is wobbling on the tray yet. A very tidy tray!");
                }
                for (int i = 1; i <= taskList.size(); i++) {
                    Task task = taskList.get(i);
                    System.out.println(i + "." + task);
                }
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                try {
                    handleDeleteCommand(command, taskList);
                    storage.save(taskList);
                } catch (WobbleException exception) {
                    System.out.println("Wobble diagnostic: " + exception.getMessage());
                } catch (java.io.IOException exception) {
                    System.out.println("Wobble diagnostic: changes could not be saved.");
                }
            } else if (command.equals("mark") || command.startsWith("mark ")
                    || command.equals("unmark") || command.startsWith("unmark ")) {
                try {
                    handleStatusCommand(command, taskList);
                    storage.save(taskList);
                } catch (WobbleException exception) {
                    System.out.println("Wobble diagnostic: " + exception.getMessage());
                } catch (java.io.IOException exception) {
                    System.out.println("Wobble diagnostic: changes could not be saved.");
                }
            } else {
                try {
                    Task task = createTask(command);
                    taskList.add(task);
                    storage.save(taskList);
                    System.out.println("Beep boop! Got it. I've added this task to my memory tray:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + taskList.size() + " tasks in the list.");
                } catch (WobbleException exception) {
                    System.out.println("Wobble diagnostic: " + exception.getMessage());
                } catch (java.io.IOException exception) {
                    System.out.println("Wobble diagnostic: task could not be saved.");
                }
            }
        }
        scanner.close();
    }

    /** Converts a command into the appropriate task subtype. */
    private static Task createTask(String command) throws WobbleException {
        if (command.isBlank()) {
            throw new WobbleException("a task command cannot be empty.");
        }
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.length() > 4 ? command.substring(4).trim() : "";
            if (description.isEmpty()) {
                throw new WobbleException("a todo description cannot be empty.");
            }
            return new Todo(description);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            int separator = command.indexOf(" /by ");
            if (separator < 0) {
                throw new WobbleException("a deadline must use: deadline <description> /by <date>");
            }
            String description = command.substring(9, separator).trim();
            String by = command.substring(separator + 5).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new WobbleException("a deadline needs both a description and a /by date");
            }
            return new Deadline(description, by);
        }
        if (command.equals("event") || command.startsWith("event ")) {
            int fromSeparator = command.indexOf(" /from ");
            int toSeparator = command.indexOf(" /to ");
            if (fromSeparator < 0 || toSeparator < 0 || fromSeparator >= toSeparator) {
                throw new WobbleException("an event must use: event <description> /from <start> /to <end>");
            }
            String description = command.substring(6, fromSeparator).trim();
            String from = command.substring(fromSeparator + 7, toSeparator).trim();
            String to = command.substring(toSeparator + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time");
            }
            return new Event(description, from, to);
        }
        throw new WobbleException("I do not know that command. Try todo, deadline, event, list, mark, unmark, delete, or bye.");
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
