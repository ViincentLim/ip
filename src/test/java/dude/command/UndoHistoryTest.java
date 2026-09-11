package dude.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dude.storage.Storage;
import dude.task.Task;
import dude.task.TaskList;
import dude.task.Todo;
import dude.ui.Ui;

/**
 * Tests session-based undo behavior.
 */
public class UndoHistoryTest {
    @Test
    public void addCommand_undoRemovesTask() throws Exception {
        TaskList tasks = new TaskList();
        UndoHistory history = new UndoHistory();
        AddCommand command = new AddCommand(CommandType.TODO, "read book");

        command.execute(tasks, testUi(), new RecordingStorage(), history);
        history.undo(tasks, testUi(), new RecordingStorage());

        assertEquals(0, tasks.size());
    }

    @Test
    public void deadlineAndEventCommands_undoRemoveTasks() throws Exception {
        TaskList tasks = new TaskList();
        UndoHistory history = new UndoHistory();
        RecordingStorage storage = new RecordingStorage();

        new AddCommand(CommandType.DEADLINE, "return book /by 2019-12-02")
                .execute(tasks, testUi(), storage, history);
        new UndoCommand(null).execute(tasks, testUi(), storage, history);
        new AddCommand(CommandType.EVENT, "project /from 2019-12-01 /to 2019-12-03")
                .execute(tasks, testUi(), storage, history);
        new UndoCommand(null).execute(tasks, testUi(), storage, history);

        assertEquals(0, tasks.size());
    }

    @Test
    public void deleteCommand_undoRestoresOriginalPosition() throws Exception {
        Task first = new Todo("first");
        Task deleted = new Todo("deleted");
        Task last = new Todo("last");
        TaskList tasks = new TaskList(List.of(first, deleted, last));
        UndoHistory history = new UndoHistory();

        new DeleteCommand("2").execute(tasks, testUi(), new RecordingStorage(), history);
        history.undo(tasks, testUi(), new RecordingStorage());

        assertEquals(List.of(first, deleted, last), tasks.asList());
    }

    @Test
    public void markAndUnmark_undoRestoresPreviousState() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        UndoHistory history = new UndoHistory();
        RecordingStorage storage = new RecordingStorage();

        new MarkCommand("1").execute(tasks, testUi(), storage, history);
        new UndoCommand(null).execute(tasks, testUi(), storage, history);
        assertFalse(tasks.get(0).isDone());

        new UnmarkCommand("1").execute(tasks, testUi(), storage, history);
        new UndoCommand(null).execute(tasks, testUi(), storage, history);
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void undo_isLastInFirstOut() throws Exception {
        TaskList tasks = new TaskList();
        UndoHistory history = new UndoHistory();
        RecordingStorage storage = new RecordingStorage();

        new AddCommand(CommandType.TODO, "first").execute(tasks, testUi(), storage, history);
        new AddCommand(CommandType.TODO, "second").execute(tasks, testUi(), storage, history);
        history.undo(tasks, testUi(), storage);
        assertEquals(List.of("first"), descriptions(tasks));
        history.undo(tasks, testUi(), storage);
        assertEquals(List.of(), descriptions(tasks));
    }

    @Test
    public void readOnlyCommands_doNotChangeHistory() throws Exception {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        UndoHistory history = new UndoHistory();

        new ListCommand(null).execute(tasks, testUi(), new RecordingStorage(), history);
        new FindCommand("book").execute(tasks, testUi(), new RecordingStorage(), history);

        assertEquals(0, history.size());
    }

    @Test
    public void emptyHistory_doesNotChangeTasks() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));
        UndoHistory history = new UndoHistory();

        history.undo(tasks, testUi(), new RecordingStorage());

        assertEquals(List.of("read book"), descriptions(tasks));
    }

    @Test
    public void failedUndoSave_restoresTaskAndKeepsHistory() throws Exception {
        TaskList tasks = new TaskList();
        UndoHistory history = new UndoHistory();
        new AddCommand(CommandType.TODO, "read book")
                .execute(tasks, testUi(), new RecordingStorage(), history);

        history.undo(tasks, testUi(), new FailingStorage());

        assertEquals(List.of("read book"), descriptions(tasks));
        assertEquals(1, history.size());
    }

    private static List<String> descriptions(TaskList tasks) {
        return tasks.asList().stream().map(Task::getDescription).toList();
    }

    private static Ui testUi() {
        return new Ui(new Scanner(""), new PrintStream(new ByteArrayOutputStream()));
    }

    private static class RecordingStorage extends Storage {
        private int saveCount;

        @Override
        public void saveTasks(TaskList tasks) {
            saveCount++;
            assertTrue(saveCount > 0);
        }
    }

    private static class FailingStorage extends Storage {
        @Override
        public void saveTasks(TaskList tasks) throws IOException {
            throw new IOException("test failure");
        }
    }
}
