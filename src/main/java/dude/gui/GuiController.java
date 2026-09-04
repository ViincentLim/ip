package dude.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import dude.command.Command;
import dude.exception.UsageException;
import dude.parser.Parser;
import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.ui.Ui;

/**
 * Connects the JavaFX controls to DUDE's existing command and storage layers.
 */
public class GuiController {
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a controller using the default storage location.
     */
    public GuiController() {
        storage = new Storage();
        tasks = new TaskList();
    }

    /**
     * Loads tasks from persistent storage.
     */
    public void loadTasks() {
        try {
            tasks = storage.loadTasks();
        } catch (IOException exception) {
            tasks = new TaskList();
        }
    }

    /**
     * Executes one existing DUDE command and captures its normal response.
     *
     * @param input Command entered in the GUI.
     * @return Text response to display.
     */
    public String execute(String input) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            Ui outputUi = new Ui(new Scanner(""), output);
            try {
                Command command = Parser.parse(input);
                command.execute(tasks, outputUi, storage);
            } catch (UsageException exception) {
                outputUi.showError(exception);
            }
        }
        return buffer.toString(StandardCharsets.UTF_8) + System.lineSeparator();
    }

    /**
     * Returns the current tasks in a format suitable for the GUI.
     *
     * @return Numbered task list, or a message when there are no tasks.
     */
    public String renderTasks() {
        if (tasks.size() == 0) {
            return "No tasks yet.";
        }
        StringBuilder result = new StringBuilder();
        for (int index = 0; index < tasks.size(); index++) {
            Task task = tasks.get(index);
            result.append(index + 1).append('.').append(task).append(System.lineSeparator());
        }
        return result.toString();
    }
}
