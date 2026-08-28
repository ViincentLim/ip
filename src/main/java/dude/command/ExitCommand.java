package dude.command;

import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;

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

    /**
     * Displays the goodbye message.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that this command terminates the application.
     *
     * @return Always true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
