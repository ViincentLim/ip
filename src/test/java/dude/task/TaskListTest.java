package dude.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests task-list collection and search behavior.
 */
public class TaskListTest {
    @Test
    public void find_isCaseInsensitive() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("buy milk")));

        assertEquals(List.of("read book"), tasks.find("BOOK").stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void find_preservesOriginalOrder() {
        TaskList tasks = new TaskList(List.of(
                new Todo("read book"),
                new Todo("return book"),
                new Todo("buy milk")));

        assertEquals(List.of("read book", "return book"), tasks.find("book").stream()
                .map(Task::getDescription)
                .toList());
    }

    @Test
    public void find_returnsEmptyListWhenThereAreNoMatches() {
        TaskList tasks = new TaskList(List.of(new Todo("read book")));

        assertEquals(List.of(), tasks.find("train"));
    }
}
