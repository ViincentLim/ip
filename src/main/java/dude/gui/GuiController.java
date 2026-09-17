package dude.gui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import dude.command.Command;
import dude.command.DuplicateResolutionHandler;
import dude.command.UndoHistory;
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
    private final UndoHistory history;
    private final DuplicateResolutionHandler resolutionHandler;
    private TaskList tasks;
    private String loadMessage = "";

    /**
     * Creates a controller using the default storage location.
     */
    public GuiController() {
        this(new Storage(), new DialogDuplicateResolutionHandler());
    }

    /**
     * Creates a controller with an injected duplicate-conflict interaction handler.
     *
     * @param resolutionHandler Handler used when adding a duplicate task.
     */
    public GuiController(DuplicateResolutionHandler resolutionHandler) {
        this(new Storage(), resolutionHandler);
    }

    /**
     * Creates a controller with explicit storage and duplicate interaction dependencies.
     *
     * @param storage Storage used by commands.
     * @param resolutionHandler Handler used when adding a duplicate task.
     */
    public GuiController(Storage storage, DuplicateResolutionHandler resolutionHandler) {
        this.storage = storage;
        history = new UndoHistory();
        this.resolutionHandler = resolutionHandler;
        tasks = new TaskList();
    }

    /**
     * Loads tasks from persistent storage.
     */
    public void loadTasks() {
        try {
            tasks = storage.loadTasks();
            loadMessage = "";
        } catch (IOException exception) {
            tasks = new TaskList();
            loadMessage = "I couldn't load your tasks, dude. Starting with an empty task list.";
        }
    }

    /**
     * Returns a startup storage message for the GUI response area.
     *
     * @return Empty text when loading succeeded, otherwise an error message.
     */
    public String getLoadMessage() {
        return loadMessage;
    }

    /**
     * Executes one existing DUDE command and captures its normal response.
     *
     * @param input Command entered in the GUI.
     * @return Text response and whether the command failed validation.
     */
    public GuiResponse execute(String input) {
        boolean error = false;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (PrintStream output = new PrintStream(buffer, true, StandardCharsets.UTF_8)) {
            Ui outputUi = new Ui(new Scanner(""), output);
            try {
                Command command = Parser.parse(input);
                command.execute(tasks, outputUi, storage, history, resolutionHandler);
            } catch (UsageException exception) {
                outputUi.showError(exception);
                error = true;
            }
        }
        return new GuiResponse(removeTerminalFormatting(buffer.toString(StandardCharsets.UTF_8)), error);
    }

    /**
     * Removes terminal-only colour control sequences before output enters the GUI.
     *
     * @param output Captured console output.
     * @return Text suitable for JavaFX labels.
     */
    private static String removeTerminalFormatting(String output) {
        return output.replaceAll("\\u001B\\[[;\\d]*m", "");
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
