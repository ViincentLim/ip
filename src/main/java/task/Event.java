package task;

import exception.UsageException;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private static final String USAGE_MESSAGE = "Usage: event <description> /from <start> /to <end>";

    /**
     * Date or time when this event starts.
     */
    protected String from;

    /**
     * Date or time when this event ends.
     */
    protected String to;

    /**
     * Creates an incomplete event task with its start and end dates or times.
     *
     * @param description Text describing the event.
     * @param from        Date or time when the event starts.
     * @param to          Date or time when the event ends.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Creates an event task from command input.
     *
     * @param input Raw text following the event command.
     * @return Parsed event task.
     * @throws UsageException If the input does not contain valid event details.
     */
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

    private static UsageException usageError(String fieldName, String actualValue,
            String expectedType, String usageToken) {
        return new UsageException("event", fieldName,
                actualValue, expectedType, USAGE_MESSAGE, usageToken);
    }

    /**
     * Returns the start date or time of this event.
     *
     * @return Event start date or time.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the end date or time of this event.
     *
     * @return Event end date or time.
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }
}
