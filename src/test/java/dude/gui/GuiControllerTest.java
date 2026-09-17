package dude.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import dude.command.DuplicateResolution;
import dude.command.DuplicateResolutionHandler;
import dude.storage.Storage;

/**
 * Tests GUI command execution without requiring a JavaFX toolkit.
 */
public class GuiControllerTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void execute_updatesTaskListAndKeepsFindIndexes() {
        GuiController controller = createController();
        controller.loadTasks();

        controller.execute("todo buy milk");
        controller.execute("todo read book");
        GuiResponse response = controller.execute("find book");

        assertFalse(response.error());
        assertTrue(response.text().contains("2.[T][ ] read book"));
        assertFalse(response.text().contains("1.[T][ ] read book"));
    }

    @Test
    public void execute_returnsStyledErrorResultForInvalidCommand() {
        GuiController controller = createController();

        GuiResponse response = controller.execute("mark missing");

        assertTrue(response.error());
        assertTrue(response.text().contains("Error: invalid task number"));
        assertFalse(response.text().contains("\\u001B"));
    }

    @Test
    public void execute_persistsTaskChanges() throws Exception {
        GuiController controller = createController();
        controller.loadTasks();
        controller.execute("todo write tests");

        Storage storage = new Storage(temporaryDirectory.resolve("duke.jsonl"),
                temporaryDirectory.resolve("duke.txt"));

        assertTrue(storage.loadTasks().get(0).getDescription().equals("write tests"));
    }

    private GuiController createController() {
        Storage storage = new Storage(temporaryDirectory.resolve("duke.jsonl"),
                temporaryDirectory.resolve("duke.txt"));
        DuplicateResolutionHandler handler = conflict -> DuplicateResolution.add();
        return new GuiController(storage, handler);
    }
}
