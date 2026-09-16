package dude.command;

import java.util.List;

import dude.task.Task;

/**
 * Contains a newly parsed task and the existing tasks with the same normalized description.
 *
 * @param candidate New task that the user attempted to add.
 * @param matches   Existing matching tasks and their list indexes.
 */
public record DuplicateTaskConflict(Task candidate, List<DuplicateMatch> matches) {
    /**
     * A matching task and its zero-based position in the task list.
     *
     * @param index Zero-based task-list index.
     * @param task  Existing matching task.
     */
    public record DuplicateMatch(int index, Task task) {
    }
}
