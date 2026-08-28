import exception.UsageException;
import storage.Storage;
import task.Task;
import task.TaskList;

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

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        Task task = tasks.get(parseTaskIndex(tasks, CommandType.UNMARK));
        task.markAsNotDone();
        ui.showUpdatedTask(task, CommandType.UNMARK);
        save(tasks, ui, storage);
    }
}
