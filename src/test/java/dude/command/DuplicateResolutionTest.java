package dude.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dude.storage.Storage;
import dude.task.TaskList;
import dude.task.Todo;
import dude.ui.Ui;

/**
 * Tests shared duplicate-resolution behaviour and its console adapter.
 */
public class DuplicateResolutionTest {
    @Test
    public void cancelDuplicate_doesNotChangeTaskList() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        new AddCommand(CommandType.TODO, "  READ   BOOK  ")
                .execute(tasks, testUi(), new RecordingStorage(), new UndoHistory(),
                        conflict -> DuplicateResolution.cancel());

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void editDuplicate_preservesDoneStateAndCanBeUndone() throws Exception {
        Todo existing = new Todo("read book");
        existing.markAsDone();
        TaskList tasks = new TaskList(List.of(existing));
        UndoHistory history = new UndoHistory();

        new AddCommand(CommandType.TODO, "read   book")
                .execute(tasks, testUi(), new RecordingStorage(), history,
                        conflict -> DuplicateResolution.edit(0));

        assertTrue(tasks.get(0).isDone());
        history.undo(tasks, testUi(), new RecordingStorage());
        assertEquals("read book", tasks.get(0).getDescription());
        assertTrue(tasks.get(0).isDone());
    }

    @Test
    public void consoleHandler_acceptsPartialAddChoice() {
        Ui ui = new Ui(new Scanner("AD\n"), new PrintStream(new ByteArrayOutputStream()));
        DuplicateTaskConflict conflict = new DuplicateTaskConflict(new Todo("read book"),
                List.of(new DuplicateTaskConflict.DuplicateMatch(0, new Todo("read book"))));

        DuplicateResolution resolution = new ConsoleDuplicateResolutionHandler(ui).resolve(conflict);

        assertEquals(DuplicateResolution.Action.ADD, resolution.action());
        assertFalse(resolution.action() == DuplicateResolution.Action.CANCEL);
    }

    private static Ui testUi() {
        return new Ui(new Scanner(""), new PrintStream(new ByteArrayOutputStream()));
    }

    private static class RecordingStorage extends Storage {
        @Override
        public void saveTasks(TaskList tasks) {
            // Persistence is not part of these in-memory interaction tests.
        }
    }
}
