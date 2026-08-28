package dude.task;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import dude.exception.UsageException;

/**
 * A task that starts and ends at specified dates or times.
 */
public class Event extends Task {
    private static final String USAGE_MESSAGE = "Usage: event <description> /from"
            + " <yyyy-MM-dd [HHmm]> /to <yyyy-MM-dd [HHmm]>";

    /**
     * Date or time when this event starts.
     */
    protected TaskDate from;

    /**
     * Date or time when this event ends.
     */
    protected TaskDate to;

    /**
     * Creates an incomplete event task with its start and end dates or times.
     *
     * @param description Text describing the event.
     * @param from        Date or time when the event starts.
     * @param to          Date or time when the event ends.
     */
    public Event(String description, TaskDate from, TaskDate to) {
        super(description);
        if (to.effectiveDateTime().isBefore(from.effectiveDateTime())) {
            throw new IllegalArgumentException("Event end precedes event start");
        }
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

        TaskDate from;
        try {
            from = TaskDate.parse(timeParts[0]);
        } catch (DateTimeParseException exception) {
            throw usageError("from", timeParts[0],
                    "yyyy-MM-dd or yyyy-MM-dd HHmm", "<yyyy-MM-dd [HHmm]>", exception);
        }

        TaskDate to;
        try {
            to = TaskDate.parse(timeParts[1]);
        } catch (DateTimeParseException exception) {
            throw usageError("to", timeParts[1],
                    "yyyy-MM-dd or yyyy-MM-dd HHmm", "<yyyy-MM-dd [HHmm]>", exception);
        }

        try {
            return new Event(eventParts[0], from, to);
        } catch (IllegalArgumentException exception) {
            throw usageError("to", timeParts[1], "a date on or after the start date",
                    "<yyyy-MM-dd [HHmm]>", exception);
        }
    }

    private static UsageException usageError(String fieldName, String actualValue,
            String expectedType, String usageToken) {
        return new UsageException("event", fieldName,
                actualValue, expectedType, USAGE_MESSAGE, usageToken);
    }

    /**
     * Returns a usage exception for an invalid date with its parsing cause.
     */
    private static UsageException usageError(String fieldName, String actualValue,
            String expectedType, String usageToken, Throwable cause) {
        return new UsageException("event", fieldName,
                actualValue, expectedType, USAGE_MESSAGE, usageToken, cause);
    }

    /**
     * Returns the start date or time of this event.
     *
     * @return Event start date or time.
     */
    public TaskDate getFrom() {
        return from;
    }

    /**
     * Returns the end date or time of this event.
     *
     * @return Event end date or time.
     */
    public TaskDate getTo() {
        return to;
    }

    /**
     * Returns whether this event occurs on the supplied date, including dates between its endpoints.
     *
     * @param targetDate Date to compare with.
     * @return True when the event includes the target date.
     */
    public boolean occursOn(LocalDate targetDate) {
        return !targetDate.isBefore(from.date()) && !targetDate.isAfter(to.date());
    }

    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(), from, to);
    }
}
