package dude.ui;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dude.command.CommandType;
import dude.exception.UsageException;
import dude.task.Deadline;
import dude.task.Event;
import dude.task.Task;
import dude.task.TaskList;

/**
 * Handles console input and user-facing output.
 */
public class Ui {
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private final Scanner scanner;
    private final PrintStream output;
    private final String border;
    private boolean isAtDivider;

    /**
     * Creates a UI connected to standard input.
     */
    public Ui() {
        this(new Scanner(System.in), System.out);
    }

    /**
     * Creates a UI connected to a supplied scanner.
     *
     * @param scanner Source of user commands.
     */
    public Ui(Scanner scanner) {
        this(scanner, System.out);
    }

    /**
     * Creates a UI connected to supplied input and output streams.
     *
     * @param scanner Source of user commands.
     * @param output  Destination for user-facing output.
     */
    public Ui(Scanner scanner, PrintStream output) {
        this.scanner = scanner;
        this.output = Objects.requireNonNull(output);
        this.border = "─".repeat(getTerminalWidth());
    }

    private static boolean occursOn(Task task, LocalDate date) {
        if (task instanceof Deadline deadline) {
            return deadline.getBy().occursOn(date);
        }
        return task instanceof Event event && event.occursOn(date);
    }

    private static int getTerminalWidth() {
        try {
            var console = System.console();
            if (console != null) {
                var method = console.getClass().getMethod("getWidth");
                return (int) method.invoke(console);
            }
        } catch (Exception ignored) {
        }
        return 60;
    }

    private static String padRight(String text, int length) {
        return text.length() >= length ? text : text + " ".repeat(length - text.length());
    }

    private static String formatActualValue(String actualValue) {
        if (actualValue == null || actualValue.equals("<missing>")) {
            return "<missing>";
        }
        return String.format("\"%s\"", actualValue);
    }

    /**
     * Displays the welcome banner and supported date formats.
     */
    public void showWelcome() {
        String banner = "██████╗  ██╗   ██╗ ██████╗  ███████╗\n"
                + "██╔══██╗ ██║   ██║ ██╔══██╗ ██╔════╝\n"
                + "██║  ██║ ██║   ██║ ██║  ██║ █████╗\n"
                + "██║  ██║ ██║   ██║ ██║  ██║ ██╔══╝\n"
                + "██████╔╝ ╚██████╔╝╚██████╔╝ "
                + "███████╗\n"
                + "╚═════╝   ╚═════╝ ╚══════╝  ╚══════╝";
        printBox(banner, "Hey! I'm DUDE, your dependable task buddy.",
                "Dates can be represented in this format: yyyy-MM-dd.",
                "To include a time, use this format: yyyy-MM-dd HHmm.",
                "What can I help you with, dude?");
    }

    /**
     * Returns whether another input line is available.
     *
     * @return True when input remains.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next non-blank command.
     *
     * @return Raw command, or null at end of input.
     */
    public String readCommand() {
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            if (!input.isBlank()) {
                return input;
            }
        }
        return null;
    }

    /**
     * Displays one divider line.
     */
    public void showLine() {
        if (!isAtDivider) {
            output.println(border);
            isAtDivider = true;
        }
    }

    /**
     * Displays the standard goodbye message.
     */
    public void showGoodbye() {
        printBox("Catch you later, dude!");
    }

    /**
     * Displays all tasks with one-based positions.
     *
     * @param tasks Application task list.
     */
    public void showTaskList(TaskList tasks) {
        String[] taskLines = Stream.concat(
                        Stream.of("Here's your task list, dude:"),
                        IntStream.range(0, tasks.size())
                                .mapToObj(i -> String.format("%d.%s", i + 1, tasks.get(i))))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    /**
     * Displays tasks whose descriptions contain a keyword.
     *
     * @param matchingTasks Tasks matching the search keyword.
     */
    public void showMatchingTasks(List<Task> matchingTasks) {
        String[] taskLines = Stream.concat(
                        Stream.of("Here are the tasks I found, dude:"),
                        IntStream.range(0, matchingTasks.size())
                                .mapToObj(i -> String.format("%d.%s", i + 1, matchingTasks.get(i))))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    /**
     * Displays tasks occurring on a date.
     *
     * @param tasks Application task list.
     * @param date  Date to match.
     */
    public void showTasksOnDate(TaskList tasks, LocalDate date) {
        String[] taskLines = Stream.concat(
                        Stream.of(String.format("Here's what you have on %s:", date)),
                        IntStream.range(0, tasks.size())
                                .filter(index -> occursOn(tasks.get(index), date))
                                .mapToObj(index -> String.format("%d.%s", index + 1, tasks.get(index))))
                .toArray(String[]::new);
        printBox(taskLines);
    }

    /**
     * Displays a successful task addition.
     *
     * @param task      Added task.
     * @param taskCount Number of tasks after the addition.
     */
    public void showAddedTask(Task task, int taskCount) {
        printBox("Nice, dude — I've added this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    /**
     * Displays a successful mark or unmark operation.
     *
     * @param task        Updated task.
     * @param commandType Mark or unmark command type.
     */
    public void showUpdatedTask(Task task, CommandType commandType) {
        String message = commandType == CommandType.MARK
                ? "Solid work, dude — this task is done:"
                : "No worries, dude — this task is back in progress:";
        printBox(message, "  " + task);
    }

    /**
     * Displays a successful deletion.
     *
     * @param task      Deleted task.
     * @param taskCount Number of tasks after deletion.
     */
    public void showDeletedTask(Task task, int taskCount) {
        printBox("All right, dude — I've removed this task:",
                "  " + task,
                String.format("Now you have %d tasks in the list.", taskCount));
    }

    /**
     * Displays a successful undo operation.
     *
     * @param description Description of the reversed command.
     */
    public void showUndo(String description) {
        printBox("Done, dude — I rolled back:", "  " + description + ".");
    }

    /**
     * Displays that there are no commands available to undo.
     */
    public void showUndoUnavailable() {
        printBox("Nothing to undo yet, dude.");
    }

    /**
     * Displays a loading failure.
     */
    public void showLoadingError() {
        printBox("I couldn't load your tasks, dude.",
                "Starting with an empty task list.");
    }

    /**
     * Displays a saving failure.
     */
    public void showSavingError() {
        printBox("I couldn't save your tasks, dude.",
                "The change remains in memory for this session.");
    }

    /**
     * Displays a structured command error.
     *
     * @param exception Error to display.
     */
    public void showError(UsageException exception) {
        String usage = exception.getUsageMessage().replace(
                exception.getUsageToken(), ANSI_RED + exception.getUsageToken() + ANSI_RESET);
        String actualValue = formatActualValue(exception.getActualValue());
        if ("command".equals(exception.getFieldName())) {
            printBox(String.format("Error: invalid command %s.", actualValue),
                    String.format("Expected: %s.", exception.getExpectedType()), usage);
            return;
        }
        printBox(String.format("Error: invalid %s %s for %s.", exception.getFieldName(),
                        actualValue, exception.getAction()),
                String.format("Expected: %s.", exception.getExpectedType()), usage);
    }

    private void printBox(String... lines) {
        output.println(border);
        isAtDivider = true;
        for (String line : lines) {
            for (String part : line.split("\n")) {
                output.println(padRight(part, border.length()));
            }
        }
        isAtDivider = false;
        showLine();
    }
}
