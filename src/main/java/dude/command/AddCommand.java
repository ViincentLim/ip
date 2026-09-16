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

    /**
     * Creates and stores the requested task type.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the task details are invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, UndoHistory history)
            throws UsageException {
        execute(tasks, ui, storage, history, new ConsoleDuplicateResolutionHandler(ui));
    }

    /**
     * Creates and stores a task using the supplied interface-specific conflict handler.
     *
     * @param tasks             Application task list.
     * @param ui                User-interface handler.
     * @param storage           Persistence handler.
     * @param history           Session undo history.
     * @param resolutionHandler Duplicate-conflict interaction handler.
     * @throws UsageException If the task details are invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, UndoHistory history,
            DuplicateResolutionHandler resolutionHandler) throws UsageException {
        Task task = switch (commandType) {
            case TODO -> Todo.fromInput(argument);
            case DEADLINE -> Deadline.fromInput(argument);
            case EVENT -> Event.fromInput(argument);
            default -> throw new IllegalArgumentException("Unsupported add command");
        };

        java.util.List<Integer> duplicateIndexes = tasks.findDuplicateIndexes(task.getDescription());
        if (!duplicateIndexes.isEmpty()) {
            java.util.List<DuplicateTaskConflict.DuplicateMatch> matches = duplicateIndexes.stream()
                    .map(index -> new DuplicateTaskConflict.DuplicateMatch(index, tasks.get(index)))
                    .toList();
            DuplicateResolution resolution = resolutionHandler.resolve(
                    new DuplicateTaskConflict(task, matches));
            if (resolution.action() == DuplicateResolution.Action.CANCEL) {
                return;
            }
            if (resolution.action() == DuplicateResolution.Action.EDIT) {
                Task previousTask = tasks.get(resolution.taskIndex());
                if (previousTask.isDone()) {
                    task.markAsDone();
                }
                tasks.replace(resolution.taskIndex(), task);
                ui.showEditedTask(task);
                history.record(new UndoAction("edit duplicate task",
                        list -> list.replace(resolution.taskIndex(), previousTask),
                        list -> list.replace(resolution.taskIndex(), task)));
                save(tasks, ui, storage);
                return;
            }
        }

        addAndShow(task, tasks, ui);
        int index = tasks.size() - 1;
        history.record(new UndoAction("add task", list -> list.remove(index),
                list -> list.insert(index, task)));
        save(tasks, ui, storage);
    }
}
