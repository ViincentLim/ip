package task;

import exception.UsageException;

/** A task that starts and ends at specified dates or times. */
public class Event extends Task {
    private static final String USAGE_MESSAGE = "Usage: event <description> /from <start> /to <end>";

    protected String from;
    protected String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    public static Event fromInput(String input) throws UsageException {
        if (input == null || input.isBlank() || input.trim().startsWith("/from")) {
            throw usageError("task details", "<missing>", "non-empty text", "<task details>");
        }

        String trimmed = input.trim();
        String[] eventParts = splitAt(trimmed, "/from");
        if (eventParts == null) {
            String token = containsStandaloneToken(trimmed, "/from") ? "<description>" : "/from";
            throw usageError("from", trimmed, "a start date/time after /from", token);
        }

        String[] timeParts = splitAt(eventParts[1], "/to");
        if (timeParts == null) {
            String token = containsStandaloneToken(eventParts[1], "/to") ? "<end>" : "/to";
            throw usageError("to", eventParts[1], "an end date/time after /to", token);
        }

        return new Event(eventParts[0], timeParts[0], timeParts[1]);
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }

    private static UsageException usageError(String fieldName, String actualValue,
                                             String expectedType, String usageToken) {
        return new UsageException("event", fieldName,
                actualValue, expectedType, USAGE_MESSAGE, usageToken);
    }
}
