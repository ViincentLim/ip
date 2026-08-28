package task;

import exception.UsageException;

/**
 * A task that must be completed by a specified date or time.
 */
public class Deadline extends Task {
    private static final String USAGE_MESSAGE = "Usage: deadline <description> /by <date or time>";

    /**
     * Date or time by which this task should be completed.
     */
    protected String by;

    /**
     * Creates an incomplete deadline task with its completion date or time.
     *
     * @param description Text describing the task.
     * @param by          Date or time by which the task should be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Creates a deadline task from command input.
     *
     * @param input Raw text following the deadline command.
     * @return Parsed deadline task.
     * @throws UsageException If the input does not contain a valid description and deadline.
     */
    public static Deadline fromInput(String input) throws UsageException {
        if (input == null || input.isBlank() || input.trim().startsWith("/by")) {
            throw usageError("task details", "<missing>", "non-empty text", "<task details>");
        }

        String trimmed = input.trim();
        String[] deadlineParts = splitAt(trimmed, "/by");
        if (deadlineParts == null) {
            String token = containsStandaloneToken(trimmed, "/by") ? "<date or time>" : "/by";
            throw usageError("by", trimmed, "a date/time after /by", token);
        }

        return new Deadline(deadlineParts[0], deadlineParts[1]);
    }

    private static UsageException usageError(String fieldName, String actualValue,
            String expectedType, String usageToken) {
        return new UsageException("deadline", fieldName, actualValue, expectedType,
                USAGE_MESSAGE, usageToken);
    }

    /**
     * Returns the date or time by which this task should be completed.
     *
     * @return Completion date or time.
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }
}
