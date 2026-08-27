import java.util.Scanner;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage();
        Parser parser = new Parser();
        TaskList taskList;
        try {
            taskList = storage.load();
        } catch (java.io.IOException exception) {
            taskList = new TaskList();
            System.out.println("Wobble diagnostic: saved tasks could not be loaded; starting with an empty tray.");
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = ui.readCommand(scanner);

            if (command.equals("bye")) {
                ui.showGoodbye();
                break;
            }

            if (command.equals("due on") || command.startsWith("due on ")) {
                try {
                    handleDateCommand(command, taskList, parser);
                } catch (WobbleException exception) {
                    System.out.println("Wobble diagnostic: " + exception.getMessage());
                }
            } else if (command.equals("list")) {
                ui.showTasks(taskList);
            } else if (command.equals("delete") || command.startsWith("delete ")) {
                try {
                    handleDeleteCommand(command, taskList);
                    storage.save(taskList);
                } catch (WobbleException exception) {
                    ui.showDiagnostic(exception.getMessage());
                } catch (java.io.IOException exception) {
                    ui.showDiagnostic("changes could not be saved.");
                }
            } else if (command.equals("mark") || command.startsWith("mark ")
                    || command.equals("unmark") || command.startsWith("unmark ")) {
                try {
                    handleStatusCommand(command, taskList);
                    storage.save(taskList);
                } catch (WobbleException exception) {
                    ui.showDiagnostic(exception.getMessage());
                } catch (java.io.IOException exception) {
                    ui.showDiagnostic("changes could not be saved.");
                }
            } else {
                try {
                    Task task = parser.parseTask(command);
                    taskList.add(task);
                    storage.save(taskList);
                    ui.showTaskAdded(task, taskList.size());
                } catch (WobbleException exception) {
                    ui.showDiagnostic(exception.getMessage());
                } catch (java.io.IOException exception) {
                    ui.showDiagnostic("task could not be saved.");
                }
            }
        }
        scanner.close();
    }

    /** Displays deadlines and events that occur on a requested date. */
    private static void handleDateCommand(String command, TaskList taskList, Parser parser) throws WobbleException {
        java.time.LocalDate date = parser.parseDueDate(command);
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
