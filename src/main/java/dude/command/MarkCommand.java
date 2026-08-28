package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Executes the mark command.
 */
public class MarkCommand extends Command {
    /**
     * Creates a mark command.
     *
     * @param argument Task number.
     */
    public MarkCommand(String argument) {
        super(argument);
    }

    /**
     * Marks and persists the selected task.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        Task task = tasks.get(parseTaskIndex(tasks, CommandType.MARK));
        task.markAsDone();
        ui.showUpdatedTask(task, CommandType.MARK);
        save(tasks, ui, storage);
    }
}
