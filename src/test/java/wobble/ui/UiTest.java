package wobble.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import wobble.tasks.TaskList;
import wobble.tasks.Todo;

/** Tests the UI's input and output behavior. */
class UiTest {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;

    @BeforeEach
    void redirectOutput() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    void readCommand_inputAvailable_returnsNextLine() {
        Ui ui = new Ui();
        Scanner scanner = new Scanner("todo read book\nbye\n");
        assertEquals("todo read book", ui.readCommand(scanner));
        assertEquals("bye", ui.readCommand(scanner));
        scanner.close();
    }

    @Test
    void readCommand_inputExhausted_returnsNull() {
        Ui ui = new Ui();
        Scanner scanner = new Scanner("");
        assertNull(ui.readCommand(scanner));
        scanner.close();
    }

    @Test
    void showWelcome_containsWobbleGreeting() {
        new Ui().showWelcome();
        assertTrue(output.toString().contains("Hello! I'm Wobble."));
        assertTrue(output.toString().contains("Systems Online"));
    }

    @Test
    void showTasks_containsTaskAndEmptyTrayMessage() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        new Ui().showTasks(taskList);
        assertTrue(output.toString().contains("1.[T][ ] read book"));
    }

    @Test
    void showTaskAdded_containsTaskAndCount() {
        new Ui().showTaskAdded(new Todo("read book"), 1);
        assertTrue(output.toString().contains("[T][ ] read book"));
        assertTrue(output.toString().contains("Now you have 1 tasks"));
    }

    @Test
    void showDiagnostic_containsMessage() {
        new Ui().showDiagnostic("test error");
        assertEquals("Wobble diagnostic: test error\n", output.toString());
    }

    @Test
    void showGoodbye_containsGoodbyeMessage() {
        new Ui().showGoodbye();
        assertTrue(output.toString().contains("Bye. Hope to see you again soon!"));
    }
}
