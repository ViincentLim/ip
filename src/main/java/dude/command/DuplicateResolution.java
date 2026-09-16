package dude.command;

/**
 * Describes the action selected when a new task duplicates existing tasks.
 */
public record DuplicateResolution(Action action, int taskIndex) {
    /**
     * The available duplicate-resolution actions.
     */
    public enum Action {
        /** Replace an existing matching task. */
        EDIT,
        /** Add the new task despite the duplicate. */
        ADD,
        /** Do not change the task list. */
        CANCEL
    }

    /**
     * Creates an Add resolution.
     *
     * @return Add resolution.
     */
    public static DuplicateResolution add() {
        return new DuplicateResolution(Action.ADD, -1);
    }

    /**
     * Creates a Cancel resolution.
     *
     * @return Cancel resolution.
     */
    public static DuplicateResolution cancel() {
        return new DuplicateResolution(Action.CANCEL, -1);
    }

    /**
     * Creates an Edit resolution.
     *
     * @param taskIndex Zero-based index of the matching task to replace.
     * @return Edit resolution.
     */
    public static DuplicateResolution edit(int taskIndex) {
        return new DuplicateResolution(Action.EDIT, taskIndex);
    }
}
