import java.util.Scanner;

public class Wobble {
    public static void main(String[] args) {
        System.out.println("==============================");
        System.out.println("  WOBBL-E // systems online");
        System.out.println("==============================");
        System.out.println("Hello! I'm Wobble.");
        System.out.println("Beep boop! Your friendly little robot companion is ready.");
        System.out.println("What can I do for you?");
        System.out.println("==============================");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equals("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("Wobble powering down... beep!");
                System.out.println("==============================");
                break;
            }

            System.out.println(command);
        }
        scanner.close();
    }
}
