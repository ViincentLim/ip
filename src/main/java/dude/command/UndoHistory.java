package dude.command;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Stores reversible task-list mutations for one application session.
 */
public class UndoHistory {
    private final Deque<UndoAction> actions = new ArrayDeque<>();

    /**
     * Adds a newly completed mutation to the top of the history.
     *
     * @param action Reversible mutation.
     */
    public void record(UndoAction action) {
        actions.push(action);
    }

    /**
     * Undoes the most recent mutation and persists the restored task list.
     *
     * @param tasks   Task list to restore.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     */
    public void undo(TaskList tasks, Ui ui, Storage storage) {
        if (actions.isEmpty()) {
            ui.showUndoUnavailable();
            return;
        }

        UndoAction action = actions.peek();
        action.undo(tasks);
        try {
            storage.saveTasks(tasks);
            actions.pop();
            ui.showUndo(action.getDescription());
        } catch (IOException exception) {
            action.redo(tasks);
            ui.showSavingError();
        }
    }

    /**
     * Returns the number of mutations available to undo.
     *
     * @return Number of undoable mutations.
     */
    public int size() {
        return actions.size();
    }
}
