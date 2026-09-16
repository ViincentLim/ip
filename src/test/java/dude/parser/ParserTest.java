package dude.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dude.command.UndoCommand;
import dude.command.CommandType;
import dude.exception.UsageException;

/**
 * Tests command parsing.
 */
public class ParserTest {
    @Test
    public void parseUndo_returnsUndoCommand() throws Exception {
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
    }

    @Test
    public void parseListWithArgument_rejectsUnexpectedArgument() {
        UsageException exception = assertThrows(UsageException.class,
                () -> Parser.parse("list extra"));
        assertEquals(CommandType.LIST.getWord(), exception.getAction());
        assertEquals("argument", exception.getFieldName());
    }

    @Test
    public void parseByeWithArgument_rejectsUnexpectedArgument() {
        assertThrows(UsageException.class, () -> Parser.parse("bye later"));
    }

    @Test
    public void parseUndoWithArgument_rejectsUnexpectedArgument() {
        assertThrows(UsageException.class, () -> Parser.parse("undo one"));
    }

    @Test
    public void parseBlankInput_rejectsMissingCommand() {
        assertThrows(UsageException.class, () -> Parser.parse("   "));
    }
}
