package dude.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;

import dude.command.UndoCommand;

/**
 * Tests command parsing.
 */
public class ParserTest {
    @Test
    public void parseUndo_returnsUndoCommand() throws Exception {
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
    }
}
