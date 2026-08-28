package task;

/**
 * A placeholder for a task record that could not be read from storage.
 */
public class CorruptedTask extends Task {
    private static final String DESCRIPTION = "[Corrupted record]";

    /**
     * Creates a visible placeholder for one malformed storage record.
     */
    public CorruptedTask() {
        super(DESCRIPTION);
    }

    @Override
    public String toString() {
        return String.format("[C]%s", super.toString());
    }
}
