package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Reverses the most recent side-effect command.
 */
public class UndoCommand extends Command {
    /**
     * Creates an undo command.
     *
     * @param argument Ignored undo argument.
     */
    public UndoCommand(String argument) {
        super(argument);
    }

    /**
     * Applies the latest undo action.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @param history Session undo history.
     * @throws UsageException Never thrown; retained for the command contract.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, UndoHistory history)
            throws UsageException {
        history.undo(tasks, ui, storage);
    }
}
