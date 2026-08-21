public enum Command {
    BYE("bye", null),
    LIST("list", null),
    MARK("mark", "Usage: mark <task number>"),
    UNMARK("unmark", "Usage: unmark <task number>"),
    DELETE("delete", "Usage: delete <task number>"),
    TODO("todo", "Usage: todo <task details>"),
    DEADLINE("deadline", "Usage: deadline <description> /by <date or time>"),
    EVENT("event", "Usage: event <description> /from <start> /to <end>");

    private final String word;
    private final String usageMessage;

    Command(String word, String usageMessage) {
        this.word = word;
        this.usageMessage = usageMessage;
    }

    public String getWord() {
        return word;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

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
}
