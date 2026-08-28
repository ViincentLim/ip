import storage.Storage;
import task.TaskList;

/**
 * Executes the list command.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     *
     * @param argument Ignored list argument.
     */
    public ListCommand(String argument) {
        super(argument);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
