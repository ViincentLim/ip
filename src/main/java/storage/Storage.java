package storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import task.CorruptedTask;
import task.Deadline;
import task.Event;
import task.Task;
import task.TaskDate;
import task.Todo;

/**
 * Loads and saves tasks as one escaped JSON object per line.
 */
public class Storage {
    /**
     * Relative path of the file containing persisted tasks.
     */
    private static final Path DATA_FILE = Path.of("data", "duke.jsonl");

    /**
     * Relative path of the legacy task file supported during migration.
     */
    private static final Path LEGACY_DATA_FILE = Path.of("data", "duke.txt");
    private static final String TYPE_FIELD = "type";
    private static final String DONE_FIELD = "done";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String DATE_FIELD = "date";
    private static final String DATE_TIME_FIELD = "dateTime";
    private static final String BY_FIELD = "by";
    private static final String FROM_FIELD = "from";
    private static final String TO_FIELD = "to";

    /**
     * Creates a storage handler for the default task data file.
     */
    public Storage() {
    }

    private static ArrayList<Task> readTasks(Path file) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            if (line.isBlank()) {
                continue;
            }

            try {
                tasks.add(parseTask(line));
            } catch (IllegalArgumentException exception) {
                tasks.add(new CorruptedTask(line));
            }
        }
        return tasks;
    }

    /**
     * Replaces the data file with the completed temporary file.
     */
    private static void replaceDataFile(Path temporaryFile) throws IOException {
        try {
            Files.move(temporaryFile, DATA_FILE,
                    java.nio.file.StandardCopyOption.ATOMIC_MOVE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(temporaryFile, DATA_FILE,
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Converts a task into the JSON object written to one data-file line.
     */
    private static String serializeTask(Task task) {
        StringBuilder json = new StringBuilder("{\"type\":")
                .append(quote(getTaskType(task)))
                .append(",\"done\":")
                .append(task.isDone())
                .append(",\"description\":")
                .append(quote(task.getDescription()));

        if (task instanceof Deadline deadline) {
            appendDateField(json, BY_FIELD, deadline.getBy());
        } else if (task instanceof Event event) {
            appendDateField(json, FROM_FIELD, event.getFrom());
            appendDateField(json, TO_FIELD, event.getTo());
        } else if (task instanceof CorruptedTask corruptedTask) {
            appendField(json, "raw", corruptedTask.getRawContent());
        }
        return json.append("}").toString();
    }

    /**
     * Appends one escaped string field to a JSON object.
     */
    private static void appendField(StringBuilder json, String field, String value) {
        json.append(",\"").append(field).append("\":").append(quote(value));
    }

    /**
     * Appends the date and optional date-time fields for a task endpoint.
     */
    private static void appendDateField(StringBuilder json, String field, TaskDate value) {
        appendField(json, field + DATE_FIELD, value.dateValue());
        appendField(json, field + DATE_TIME_FIELD, value.dateTimeValue());
    }

    /**
     * Returns the serialized type code for a task.
     */
    private static String getTaskType(Task task) {
        if (task instanceof CorruptedTask) {
            return "C";
        } else if (task instanceof Todo) {
            return "T";
        } else if (task instanceof Deadline) {
            return "D";
        } else if (task instanceof Event) {
            return "E";
        }
        throw new IllegalArgumentException("Unsupported task type");
    }

    /**
     * Reconstructs a task from a parsed JSON object.
     */
    private static Task parseTask(String line) {
        Map<String, String> fields = new JsonObjectParser(line).parse();
        String type = requireField(fields, TYPE_FIELD);
        boolean isDone = parseBoolean(requireField(fields, DONE_FIELD));
        String description = requireField(fields, DESCRIPTION_FIELD);

        Task task = switch (type) {
            case "T" -> new Todo(description);
            case "D" -> new Deadline(description, parseDateField(fields, BY_FIELD));
            case "E" -> new Event(description,
                    parseDateField(fields, FROM_FIELD), parseDateField(fields, TO_FIELD));
            case "C" -> new CorruptedTask(fields.getOrDefault("raw", description));
            default -> throw new IllegalArgumentException("Unknown task type");
        };

        if (isDone) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Returns a required non-blank field from parsed task data.
     */
    private static String requireField(Map<String, String> fields, String field) {
        String value = fields.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing field: " + field);
        }
        return value;
    }

    /**
     * Reconstructs a task date from its JSONL fields.
     */
    private static TaskDate parseDateField(Map<String, String> fields, String field) {
        try {
            return task.TaskDate.fromStorage(
                    requireField(fields, field + DATE_FIELD), fields.get(field + DATE_TIME_FIELD));
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid date field: " + field, exception);
        }
    }

    /**
     * Converts a serialized boolean value into a Java boolean.
     */
    private static boolean parseBoolean(String value) {
        if ("true".equals(value)) {
            return true;
        } else if ("false".equals(value)) {
            return false;
        }
        throw new IllegalArgumentException("Invalid completion state");
    }

    /**
     * Escapes a string for use as a JSON string value.
     */
    private static String quote(String value) {
        StringBuilder escaped = new StringBuilder("\"");
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(character);
            }
        }
        return escaped.append('"').toString();
    }

    /**
     * Loads every valid task and represents malformed records as corrupted tasks.
     *
     * @return Tasks reconstructed from the data file.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public ArrayList<Task> loadTasks() throws IOException {
        Files.createDirectories(DATA_FILE.getParent());

        if (Files.exists(DATA_FILE)) {
            return readTasks(DATA_FILE);
        }

        if (Files.exists(LEGACY_DATA_FILE)) {
            ArrayList<Task> tasks = readTasks(LEGACY_DATA_FILE);
            saveTasks(tasks);
            return tasks;
        }

        Files.createFile(DATA_FILE);
        return new ArrayList<>();
    }

    /**
     * Saves the supplied tasks, replacing the data file only after writing completes.
     *
     * @param tasks Tasks to persist.
     * @throws IOException If the data directory or file cannot be accessed.
     */
    public void saveTasks(List<Task> tasks) throws IOException {
        Files.createDirectories(DATA_FILE.getParent());
        Path temporaryFile = Files.createTempFile(DATA_FILE.getParent(), "duke", ".tmp");

        try {
            List<String> lines = tasks.stream()
                    .map(Storage::serializeTask)
                    .toList();
            Files.write(temporaryFile, lines, StandardCharsets.UTF_8);
            replaceDataFile(temporaryFile);
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }

    /**
     * Parses the limited JSON object shape emitted by this storage class.
     */
    private static class JsonObjectParser {
        private final String input;
        private int position;

        /**
         * Creates a parser for one serialized JSON object.
         */
        JsonObjectParser(String input) {
            this.input = input.trim();
        }

        /**
         * Returns the character represented by a supported JSON escape.
         */
        private static char parseEscape(char character) {
            return switch (character) {
                case '"' -> '"';
                case '\\' -> '\\';
                case 'n' -> '\n';
                case 'r' -> '\r';
                case 't' -> '\t';
                default -> throw new IllegalArgumentException("Unsupported escape");
            };
        }

        /**
         * Returns the fields parsed from the JSON object.
         */
        Map<String, String> parse() {
            Map<String, String> fields = new HashMap<>();
            expect('{');
            skipWhitespace();
            if (consume('}')) {
                return fields;
            }

            while (true) {
                String key = parseString();
                skipWhitespace();
                expect(':');
                skipWhitespace();
                fields.put(key, parseValue());
                skipWhitespace();
                if (consume('}')) {
                    ensureEnd();
                    return fields;
                }
                expect(',');
                skipWhitespace();
            }
        }

        /**
         * Returns the next JSON string or literal value.
         */
        private String parseValue() {
            if (position < input.length() && input.charAt(position) == '"') {
                return parseString();
            }

            int start = position;
            while (position < input.length() && input.charAt(position) != ','
                    && input.charAt(position) != '}') {
                position++;
            }
            String value = input.substring(start, position).trim();
            if (value.isEmpty()) {
                throw new IllegalArgumentException("Missing value");
            }
            return value;
        }

        /**
         * Returns the next decoded JSON string.
         */
        private String parseString() {
            expect('"');
            StringBuilder value = new StringBuilder();
            while (position < input.length()) {
                char character = input.charAt(position++);
                if (character == '"') {
                    return value.toString();
                }
                if (character != '\\') {
                    value.append(character);
                    continue;
                }
                if (position >= input.length()) {
                    throw new IllegalArgumentException("Unterminated escape");
                }
                value.append(parseEscape(input.charAt(position++)));
            }
            throw new IllegalArgumentException("Unterminated string");
        }

        /**
         * Consumes the expected JSON character or rejects the input.
         */
        private void expect(char expected) {
            skipWhitespace();
            if (position >= input.length() || input.charAt(position++) != expected) {
                throw new IllegalArgumentException("Unexpected JSON character");
            }
        }

        /**
         * Consumes the expected character when it is next in the input.
         */
        private boolean consume(char expected) {
            if (position < input.length() && input.charAt(position) == expected) {
                position++;
                return true;
            }
            return false;
        }

        /**
         * Advances past whitespace at the current parser position.
         */
        private void skipWhitespace() {
            while (position < input.length() && Character.isWhitespace(input.charAt(position))) {
                position++;
            }
        }

        /**
         * Rejects any characters remaining after the JSON object.
         */
        private void ensureEnd() {
            skipWhitespace();
            if (position != input.length()) {
                throw new IllegalArgumentException("Unexpected trailing JSON");
            }
        }
    }
}
