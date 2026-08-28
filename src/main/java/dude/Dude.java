package dude;

import java.io.IOException;

import dude.command.Command;
import dude.exception.UsageException;
import dude.parser.Parser;
import dude.storage.Storage;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Coordinates the DUDE application components.
 */
public class Dude {
    private final Storage storage;
    private final Ui ui;
    private final Parser parser;
    private TaskList tasks;

    /**
     * Creates an application using standard input and the default storage.
     */
    public Dude() {
        this(new Storage(), new Ui(), new Parser());
    }

    /**
     * Creates an application with supplied collaborators.
     *
     * @param storage Persistence handler.
     * @param ui      User-interface handler.
     * @param parser  Command parser.
     */
    public Dude(Storage storage, Ui ui, Parser parser) {
        this.storage = storage;
        this.ui = ui;
        this.parser = parser;
        this.tasks = new TaskList();
    }

    /**
     * Starts DUDE with its default collaborators.
     *
     * @param args Command-line arguments, which are not used.
     */
    static void main(String[] args) {
        new Dude().run();
    }

    /**
     * Starts the application and processes commands until exit or input exhaustion.
     */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();

        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            try {
                String fullCommand = ui.readCommand();
                if (fullCommand == null) {
                    break;
                }
                ui.showLine();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (UsageException exception) {
                ui.showError(exception);
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Loads persisted tasks, falling back to an empty list on failure.
     */
    private TaskList loadTasks() {
        try {
            return storage.loadTasks();
        } catch (IOException exception) {
            ui.showLoadingError();
            return new TaskList();
        }
    }
}
