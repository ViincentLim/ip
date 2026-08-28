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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        Task task = tasks.remove(parseTaskIndex(tasks, CommandType.DELETE));
        ui.showDeletedTask(task, tasks.size());
        save(tasks, ui, storage);
    }
}
