package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Executes the unmark command.
 */
public class UnmarkCommand extends Command {
    /**
     * Creates an unmark command.
     *
     * @param argument Task number.
     */
    public UnmarkCommand(String argument) {
        super(argument);
    }

    /**
     * Marks and persists the selected task as incomplete.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        Task task = tasks.get(parseTaskIndex(tasks, CommandType.UNMARK));
        task.markAsNotDone();
        ui.showUpdatedTask(task, CommandType.UNMARK);
        save(tasks, ui, storage);
    }
}
