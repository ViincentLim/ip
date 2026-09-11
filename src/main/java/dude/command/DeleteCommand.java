package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Executes the delete command.
 */
public class DeleteCommand extends Command {
    /**
     * Creates a delete command.
     *
     * @param argument Task number.
     */
    public DeleteCommand(String argument) {
        super(argument);
    }

    /**
     * Removes and persists the selected task.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, UndoHistory history)
            throws UsageException {
        int index = parseTaskIndex(tasks, CommandType.DELETE);
        Task task = tasks.remove(index);
        ui.showDeletedTask(task, tasks.size());
        history.record(new UndoAction("delete task", list -> list.insert(index, task),
                list -> list.remove(index)));
        save(tasks, ui, storage);
    }
}
