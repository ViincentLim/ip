import exception.UsageException;
import storage.Storage;
import task.Deadline;
import task.Event;
import task.Task;
import task.TaskList;
import task.Todo;

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
