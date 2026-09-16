package wobble.parser;

import java.time.LocalDate;

import wobble.exceptions.WobbleException;
import wobble.tasks.Deadline;
import wobble.tasks.Event;
import wobble.tasks.Task;
import wobble.tasks.Todo;

/** Interprets user command text and creates the corresponding task data. */
public class Parser {
    /** Creates a parser for Wobble commands. */
    public Parser() {
    }

    /** Converts an add command into the appropriate task subtype. */
    public Task parseTask(String command) throws WobbleException {
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
                throw new WobbleException("a deadline needs a description and a /by date.");
            }
            String description = command.substring(9, separator).trim();
            String by = command.substring(separator + 5).trim();
            if (description.isEmpty() || by.isEmpty()) {
                throw new WobbleException("a deadline needs both a description and a /by date");
            }
            try {
                return new Deadline(description, DateTimeParser.parse(by));
            } catch (java.time.format.DateTimeParseException exception) {
                throw new WobbleException("the deadline date or time is not in a supported format.");
            }
        }
        if (command.equals("event") || command.startsWith("event ")) {
            int fromSeparator = command.indexOf(" /from ");
            int toSeparator = command.indexOf(" /to ");
            if (fromSeparator < 0 || toSeparator < 0 || fromSeparator >= toSeparator) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time.");
            }
            String description = command.substring(6, fromSeparator).trim();
            String from = command.substring(fromSeparator + 7, toSeparator).trim();
            String to = command.substring(toSeparator + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
                throw new WobbleException("an event needs a description, a /from time, and a /to time");
            }
            try {
                return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to));
            } catch (java.time.format.DateTimeParseException exception) {
                throw new WobbleException("the event date or time is not in a supported format.");
            }
        }
        throw new WobbleException("I do not recognize that command.");
    }

    /** Parses a date used by the due-on command. */
    public LocalDate parseDueDate(String command) throws WobbleException {
        String dateText = command.length() > 7 ? command.substring(7).trim() : "";
        if (dateText.isEmpty()) {
            throw new WobbleException("a due date is required.");
        }
        try {
            return DateTimeParser.parse(dateText).toLocalDate();
        } catch (java.time.format.DateTimeParseException exception) {
            throw new WobbleException("the date must use yyyy-MM-dd, yyyy.MM.dd, or yyyy/MM/dd.");
        }
    }

    /** Parses the optional day range used by the reminders command. */
    public int parseReminderDays(String command) throws WobbleException {
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 1) {
            return 7;
        }
        if (parts.length != 2) {
            throw new WobbleException("the reminders command accepts an optional number of days.");
        }
        try {
            int days = Integer.parseInt(parts[1]);
            if (days < 0) {
                throw new WobbleException("the reminder range cannot be negative");
            }
            return days;
        } catch (NumberFormatException exception) {
            throw new WobbleException("the reminder range must be a whole number of days");
        }
    }
}
