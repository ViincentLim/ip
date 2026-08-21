package task;

import exception.UsageException;

/** A task without an attached date or time. */
public class Todo extends Task {
    public Todo(String description) {
        super(description);
    }

    public static Todo fromInput(String input) throws UsageException {
        if (input == null || input.isBlank()) {
            throw usageError("<missing>", "non-empty text", "<task details>");
        }
        return new Todo(input.trim());
    }

    @Override
    public String toString() {
        return String.format("[T]%s", super.toString());
    }

    private static UsageException usageError(String actualValue, String expectedType,
                                             String usageToken) {
        return new UsageException("todo", "task details", actualValue, expectedType,
                "Usage: todo <task details>", usageToken);
    }
}
