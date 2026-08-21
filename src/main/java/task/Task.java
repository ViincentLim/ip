package task;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    private String getStatusIcon() {
        return isDone ? "X" : " ";
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", getStatusIcon(), description);
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    protected static String[] splitAt(String input, String marker) {
        Matcher matcher = Pattern.compile("^(.*?)\\s+" + Pattern.quote(marker) + "\\s+(.+)$")
                .matcher(input.trim());
        if (!matcher.matches()) {
            return null;
        }

        String before = matcher.group(1).trim();
        String after = matcher.group(2).trim();
        return before.isEmpty() || after.isEmpty() ? null : new String[] {before, after};
    }

    protected static boolean containsStandaloneToken(String input, String token) {
        return Pattern.compile("(^|\\s)" + Pattern.quote(token) + "(\\s|$)")
                .matcher(input)
                .find();
    }
}
