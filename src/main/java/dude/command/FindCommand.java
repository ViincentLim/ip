package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Executes the find command.
 */
public class FindCommand extends Command {
    /**
     * Creates a find command.
     *
     * @param argument Search keyword.
     */
    public FindCommand(String argument) {
        super(argument);
    }

    /**
     * Displays tasks whose descriptions contain the search keyword.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the keyword is missing or blank.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw new UsageException("find", "keyword", "<missing>",
                    "a non-blank keyword", CommandType.FIND.getUsageMessage(), "<keyword>");
        }
        ui.showMatchingTasks(tasks.find(argument.trim()));
    }
}
