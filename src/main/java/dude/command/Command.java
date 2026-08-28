package dude.command;

import dude.exception.UsageException;
import dude.parser.Parser;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * An executable command entered by the user.
 */
public abstract class Command {
    /**
     * Raw argument supplied to this command.
     */
    protected final String argument;

    /**
     * Creates a command with its raw argument.
     *
     * @param argument Raw argument, or null when absent.
     */
    protected Command(String argument) {
        this.argument = argument;
    }

    /**
     * Adds a task and displays the standard confirmation.
     *
     * @param task  Task to add.
     * @param tasks Application task list.
     * @param ui    User-interface handler.
     */
    protected static void addAndShow(Task task, TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }

    /**
     * Executes this command.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     * @throws UsageException If the command argument is invalid.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage)
            throws UsageException;

    /**
     * Returns whether executing this command should terminate the application.
     *
     * @return True only for the exit command.
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Parses a one-based task number and converts it to a zero-based index.
     *
     * @param tasks       Application task list.
     * @param commandType Command whose argument is being parsed.
     * @return Zero-based task index.
     * @throws UsageException If the argument is not a valid task number.
     */
    protected int parseTaskIndex(TaskList tasks, CommandType commandType)
            throws UsageException {
        return Parser.parseTaskIndex(commandType, argument, tasks.size());
    }

    /**
     * Saves the current task list and reports failures through the UI.
     *
     * @param tasks   Application task list.
     * @param ui      User-interface handler.
     * @param storage Persistence handler.
     */
    protected void save(TaskList tasks, Ui ui, Storage storage) {
        try {
            storage.saveTasks(tasks);
        } catch (java.io.IOException exception) {
            ui.showSavingError();
        }
    }
}
