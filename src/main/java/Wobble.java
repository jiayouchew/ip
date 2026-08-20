import java.util.Scanner;

/** A small chatbot that stores tasks for the current session. */
public class Wobble {
    private static final int MAX_TASKS = 100;

    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("  WOBBL-E // systems online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("My memory tray is polished and ready for tasks.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");

        String[] tasks = new String[MAX_TASKS];
        int taskCount = 0;
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
                if (taskCount == 0) {
                    System.out.println("Nothing is wobbling on the tray yet.");
                }
                for (int i = 0; i < taskCount; i++) {
                    System.out.println((i + 1) + ". " + tasks[i]);
                }
            } else if (taskCount < MAX_TASKS) {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println("added: " + command);
                System.out.println("Wobble note: safely tucked into the memory tray!");
            } else {
                System.out.println("Wobble alert: my task tray is full! Beep boop!");
            }
        }
        scanner.close();
    }
}
