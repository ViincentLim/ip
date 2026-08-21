package task;

import exception.UsageException;

/** A task that must be completed by a specified date or time. */
public class Deadline extends Task {
    private static final String USAGE_MESSAGE = "Usage: deadline <description> /by <date or time>";

    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

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

    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), by);
    }

    private static UsageException usageError(String fieldName, String actualValue,
                                             String expectedType, String usageToken) {
        return new UsageException("deadline", fieldName, actualValue, expectedType,
                USAGE_MESSAGE, usageToken);
    }
}
