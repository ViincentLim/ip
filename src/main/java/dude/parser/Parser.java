package dude.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import dude.command.AddCommand;
import dude.command.Command;
import dude.command.CommandType;
import dude.command.DeleteCommand;
import dude.command.ExitCommand;
import dude.command.ListCommand;
import dude.command.MarkCommand;
import dude.command.OnCommand;
import dude.command.UnmarkCommand;
import dude.exception.UsageException;

/**
 * Converts raw user input into executable command objects.
 */
public class Parser {
    private static final String COMMAND_USAGE = "Usage: todo <task details> | deadline <description> /by"
            + " <yyyy-MM-dd [HHmm]> | event <description> /from <yyyy-MM-dd [HHmm]>"
            + " /to <yyyy-MM-dd [HHmm]> | on <yyyy-MM-dd>";

    /**
     * Creates a parser.
     */
    public Parser() {
    }

    /**
     * Parses a full command line.
     *
     * @param input Raw command line.
     * @return Executable command.
     * @throws UsageException If the command word is unknown.
     */
    public static Command parse(String input) throws UsageException {
        String[] commandParts = input.trim().split("\\s+", 2);
        String action = commandParts[0];
        String argument = commandParts.length > 1 ? commandParts[1] : null;

        CommandType commandType = parseType(action);
        return switch (commandType) {
            case BYE -> new ExitCommand(argument);
            case LIST -> new ListCommand(argument);
            case ON -> new OnCommand(argument);
            case MARK -> new MarkCommand(argument);
            case UNMARK -> new UnmarkCommand(argument);
            case DELETE -> new DeleteCommand(argument);
            case TODO, DEADLINE, EVENT -> new AddCommand(commandType, argument);
        };
    }

    private static CommandType parseType(String action) throws UsageException {
        for (CommandType commandType : CommandType.values()) {
            if (commandType.getWord().equals(action)) {
                return commandType;
            }
        }
        throw new UsageException(action, "command", action,
                "todo, deadline, event, or on", COMMAND_USAGE, "<task type>");
    }

    /**
     * Parses a one-based task number into a zero-based index.
     *
     * @param commandType Command whose argument is being parsed.
     * @param argument    Raw task-number argument.
     * @param taskCount   Number of available tasks.
     * @return Zero-based task index.
     * @throws UsageException If the argument is not a valid task number.
     */
    public static int parseTaskIndex(CommandType commandType, String argument, int taskCount)
            throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw usageError(commandType, "task number", "<missing>", "an integer", "<task number>");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw usageError(commandType, "task number", argument,
                    "an integer", "<task number>", exception);
        }

        int taskIndex = taskNumber - 1;
        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw usageError(commandType, "task number", argument,
                    "an existing task number", "<task number>");
        }
        return taskIndex;
    }

    /**
     * Parses the date argument of the date-query command.
     *
     * @param argument Raw date argument.
     * @return Parsed date.
     * @throws UsageException If the argument is not a valid date.
     */
    public static LocalDate parseDate(String argument) throws UsageException {
        if (argument == null || argument.isBlank()) {
            throw usageError(CommandType.ON, "date", "<missing>", "yyyy-MM-dd", "<yyyy-MM-dd>");
        }
        try {
            return dude.task.TaskDate.parseDate(argument);
        } catch (DateTimeParseException exception) {
            throw usageError(CommandType.ON, "date", argument,
                    "yyyy-MM-dd", "<yyyy-MM-dd>", exception);
        }
    }

    private static UsageException usageError(CommandType commandType, String fieldName,
            String actualValue, String expectedType, String usageToken) {
        return new UsageException(commandType.getWord(), fieldName, actualValue,
                expectedType, commandType.getUsageMessage(), usageToken);
    }

    private static UsageException usageError(CommandType commandType, String fieldName,
            String actualValue, String expectedType, String usageToken, Throwable cause) {
        return new UsageException(commandType.getWord(), fieldName, actualValue,
                expectedType, commandType.getUsageMessage(), usageToken, cause);
    }
}
