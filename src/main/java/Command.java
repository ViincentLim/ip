/**
 * Represents a command accepted by the DUDE command-line application.
 */
public enum Command {
    /**
     * Terminates the application.
     */
    BYE("bye", null),
    /**
     * Displays all tasks.
     */
    LIST("list", null),
    /**
     * Marks a task as completed.
     */
    MARK("mark", "Usage: mark <task number>"),
    /**
     * Marks a task as not completed.
     */
    UNMARK("unmark", "Usage: unmark <task number>"),
    /**
     * Removes a task.
     */
    DELETE("delete", "Usage: delete <task number>"),
    /**
     * Creates a todo task.
     */
    TODO("todo", "Usage: todo <task details>"),
    /**
     * Creates a deadline task.
     */
    DEADLINE("deadline", "Usage: deadline <description> /by <date or time>"),
    /**
     * Creates an event task.
     */
    EVENT("event", "Usage: event <description> /from <start> /to <end>");

    /**
     * Text used to identify this command in user input.
     */
    private final String word;

    /**
     * Usage message displayed when this command receives invalid input.
     */
    private final String usageMessage;

    /**
     * Creates a command with its input word and usage message.
     *
     * @param word         Text used to identify the command.
     * @param usageMessage Usage message displayed for invalid command input.
     */
    Command(String word, String usageMessage) {
        this.word = word;
        this.usageMessage = usageMessage;
    }

    /**
     * Returns the command represented by the supplied input word, or null if it is unknown.
     *
     * @param word Input word to parse.
     * @return Matching command, or null when the word is unknown.
     */
    public static Command parse(String word) {
        return switch (word) {
            case "bye" -> BYE;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            default -> null;
        };
    }

    /**
     * Returns the input word associated with this command.
     *
     * @return Input word associated with this command.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns the usage message associated with this command.
     *
     * @return Usage message associated with this command.
     */
    public String getUsageMessage() {
        return usageMessage;
    }
}
