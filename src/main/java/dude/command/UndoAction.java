package dude.command;

import java.util.Objects;
import java.util.function.Consumer;

import dude.task.TaskList;

/**
 * Represents one reversible task-list mutation.
 */
public final class UndoAction {
    private final String description;
    private final Consumer<TaskList> undo;
    private final Consumer<TaskList> redo;

    /**
     * Creates a reversible task-list mutation.
     *
     * @param description Short description displayed after undoing.
     * @param undo        Operation that reverses the mutation.
     * @param redo        Operation that reapplies the mutation if persistence fails.
     */
    public UndoAction(String description, Consumer<TaskList> undo, Consumer<TaskList> redo) {
        this.description = Objects.requireNonNull(description);
        this.undo = Objects.requireNonNull(undo);
        this.redo = Objects.requireNonNull(redo);
    }

    /**
     * Returns the description of the reversed command.
     *
     * @return Undo description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Reverses the associated mutation.
     *
     * @param tasks Task list to update.
     */
    public void undo(TaskList tasks) {
        undo.accept(tasks);
    }

    /**
     * Reapplies the associated mutation.
     *
     * @param tasks Task list to update.
     */
    public void redo(TaskList tasks) {
        redo.accept(tasks);
    }
}
