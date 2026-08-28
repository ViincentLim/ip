import java.time.LocalDate;

import exception.UsageException;
import storage.Storage;
import task.TaskList;

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
