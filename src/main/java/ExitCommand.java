import storage.Storage;
import task.TaskList;

/**
 * Executes the exit command.
 */
public class ExitCommand extends Command {
    /**
     * Creates an exit command.
     *
     * @param argument Ignored exit argument.
     */
    public ExitCommand(String argument) {
        super(argument);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
