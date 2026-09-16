package wobble.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests command suggestions for misspelled and incorrectly formatted input. */
public class CommandSuggesterTest {
    @Test
    public void suggest_typoInTodoCommand_returnsCorrectedCommand() {
        assertEquals("\"todo buy milk\"", CommandSuggester.suggest("todoo buy milk"));
    }

    @Test
    public void suggest_malformedDeadline_returnsDeadlineFormat() {
        assertEquals("\"deadline <description> /by <date/time>\"",
                CommandSuggester.suggest("deadline submit report"));
    }

    @Test
    public void suggest_typoInDueOnCommand_returnsCorrectedCommand() {
        assertEquals("\"due on 2026-09-17\"", CommandSuggester.suggest("dueon 2026-09-17"));
    }

    @Test
    public void suggest_typoInRemoveCommand_returnsCorrectedCommand() {
        assertEquals("\"remove 2\"", CommandSuggester.suggest("remvoe 2"));
    }

    @Test
    public void suggest_extraTimeDigit_returnsCorrectedDeadlineCommand() {
        assertEquals("\"deadline project /by 2026-09-16 1800\"",
                CommandSuggester.suggest("deadline project /by 2026-09-16 18000"));
    }

    @Test
    public void suggest_unknownCommand_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest("blah"));
    }

    @Test
    public void suggest_emptyCommand_returnsHelpFormat() {
        assertEquals("\"help\"", CommandSuggester.suggest("   "));
    }
}
