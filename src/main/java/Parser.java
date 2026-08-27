import java.time.LocalDate;

/** Interprets user command text and creates the corresponding task data. */
public class Parser {
    /** Converts an add command into the appropriate task subtype. */
    public Task parseTask(String command) throws WobbleException {
        if (command.isBlank()) throw new WobbleException("a task command cannot be empty.");
        if (command.equals("todo") || command.startsWith("todo ")) {
            String description = command.length() > 4 ? command.substring(4).trim() : "";
            if (description.isEmpty()) throw new WobbleException("a todo description cannot be empty.");
            return new Todo(description);
        }
        if (command.equals("deadline") || command.startsWith("deadline ")) {
            int separator = command.indexOf(" /by ");
            if (separator < 0) throw new WobbleException("a deadline must use: deadline <description> /by <date>");
            String description = command.substring(9, separator).trim();
            String by = command.substring(separator + 5).trim();
            if (description.isEmpty() || by.isEmpty()) throw new WobbleException("a deadline needs both a description and a /by date");
            try { return new Deadline(description, DateTimeParser.parse(by)); }
            catch (java.time.format.DateTimeParseException exception) { throw new WobbleException("the deadline date must use yyyy-MM-dd or yyyy-MM-dd HHmm"); }
        }
        if (command.equals("event") || command.startsWith("event ")) {
            int fromSeparator = command.indexOf(" /from ");
            int toSeparator = command.indexOf(" /to ");
            if (fromSeparator < 0 || toSeparator < 0 || fromSeparator >= toSeparator) throw new WobbleException("an event must use: event <description> /from <start> /to <end>");
            String description = command.substring(6, fromSeparator).trim();
            String from = command.substring(fromSeparator + 7, toSeparator).trim();
            String to = command.substring(toSeparator + 5).trim();
            if (description.isEmpty() || from.isEmpty() || to.isEmpty()) throw new WobbleException("an event needs a description, a /from time, and a /to time");
            try { return new Event(description, DateTimeParser.parse(from), DateTimeParser.parse(to)); }
            catch (java.time.format.DateTimeParseException exception) { throw new WobbleException("event dates must use yyyy-MM-dd or yyyy-MM-dd HHmm"); }
        }
        throw new WobbleException("I do not know that command. Try todo, deadline, event, list, mark, unmark, delete, due on, or bye.");
    }

    /** Parses a date used by the due-on command. */
    public LocalDate parseDueDate(String command) throws WobbleException {
        String dateText = command.length() > 7 ? command.substring(7).trim() : "";
        if (dateText.isEmpty()) throw new WobbleException("please use due on <date>, for example: due on 2019-12-02");
        try { return DateTimeParser.parse(dateText).toLocalDate(); }
        catch (java.time.format.DateTimeParseException exception) { throw new WobbleException("the date must use yyyy-MM-dd, yyyy.MM.dd, or yyyy/MM/dd."); }
    }
}
