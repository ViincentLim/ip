package dude.command;

import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;


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
