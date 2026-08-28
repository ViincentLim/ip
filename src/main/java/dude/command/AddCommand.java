package dude.command;

import dude.exception.UsageException;
import dude.storage.Storage;
import dude.task.Deadline;
import dude.task.Event;
import dude.task.Task;
import dude.task.TaskList;
import dude.task.Todo;
import dude.ui.Ui;


/**
 * Executes todo, deadline, and event creation commands.
 */
public class AddCommand extends Command {
    private final CommandType commandType;

    /**
     * Creates an add command.
     *
     * @param commandType Type of task to create.
     * @param argument    Raw task details.
     */
    public AddCommand(CommandType commandType, String argument) {
        super(argument);
        this.commandType = commandType;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws UsageException {
        Task task = switch (commandType) {
            case TODO -> Todo.fromInput(argument);
            case DEADLINE -> Deadline.fromInput(argument);
            case EVENT -> Event.fromInput(argument);
            default -> throw new IllegalArgumentException("Unsupported add command");
        };
        addAndShow(task, tasks, ui);
        save(tasks, ui, storage);
    }
}
