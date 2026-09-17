package dude.gui;

/**
 * Result of executing a command through the GUI adapter.
 *
 * @param text Text to display in the conversation.
 * @param error Whether the response describes invalid user input.
 */
public record GuiResponse(String text, boolean error) {
}
