package dude.command;

import java.time.LocalDate;

import dude.parser.Parser;
import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;


/**
 * Executes the date-query command.
 */
public class OnCommand extends Command {
    /**
     * Creates a date-query command.
     *
     * @param argument Date argument.
     */
    public OnCommand(String argument) {
        super(argument);
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        LocalDate date = Parser.parseDate(argument);
        ui.showTasksOnDate(tasks, date);
    }
}
