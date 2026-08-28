package exception;

/**
 * Describes an invalid command field and the format expected by the command.
 */
public class UsageException extends Exception {
    /**
     * Command that received invalid input.
     */
    private final String action;

    /**
     * Name of the invalid command field.
     */
    private final String fieldName;

    /**
     * Value supplied for the invalid field.
     */
    private final String actualValue;

    /**
     * Type or format expected for the invalid field.
     */
    private final String expectedType;

    /**
     * Usage message describing the valid command format.
     */
    private final String usageMessage;

    /**
     * Token in the usage message that should be highlighted.
     */
    private final String usageToken;

    /**
     * Creates an exception describing invalid command input.
     *
     * @param action       Command that received invalid input.
     * @param fieldName    Name of the invalid command field.
     * @param actualValue  Value supplied for the invalid field.
     * @param expectedType Type or format expected for the invalid field.
     * @param usageMessage Usage message describing the valid command format.
     * @param usageToken   Token in the usage message to highlight.
     */
    public UsageException(String action, String fieldName, String actualValue,
            String expectedType, String usageMessage, String usageToken) {
        this(action, fieldName, actualValue, expectedType, usageMessage, usageToken, null);
    }

    /**
     * Creates an exception describing invalid command input with its cause.
     *
     * @param action       Command that received invalid input.
     * @param fieldName    Name of the invalid command field.
     * @param actualValue  Value supplied for the invalid field.
     * @param expectedType Type or format expected for the invalid field.
     * @param usageMessage Usage message describing the valid command format.
     * @param usageToken   Token in the usage message to highlight.
     * @param cause        Exception that caused this usage error.
     */
    public UsageException(String action, String fieldName, String actualValue,
            String expectedType, String usageMessage, String usageToken,
            Throwable cause) {
        super(String.format("Invalid %s for %s: %s; expected %s",
                fieldName, action, actualValue, expectedType), cause);
        this.action = action;
        this.fieldName = fieldName;
        this.actualValue = actualValue;
        this.expectedType = expectedType;
        this.usageMessage = usageMessage;
        this.usageToken = usageToken;
    }

    /**
     * Returns the command that received invalid input.
     *
     * @return Command that received invalid input.
     */
    public String getAction() {
        return action;
    }

    /**
     * Returns the name of the invalid command field.
     *
     * @return Name of the invalid command field.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the value supplied for the invalid field.
     *
     * @return Value supplied for the invalid field.
     */
    public String getActualValue() {
        return actualValue;
    }

    /**
     * Returns the type or format expected for the invalid field.
     *
     * @return Type or format expected for the invalid field.
     */
    public String getExpectedType() {
        return expectedType;
    }

    /**
     * Returns the usage message describing the valid command format.
     *
     * @return Usage message describing the valid command format.
     */
    public String getUsageMessage() {
        return usageMessage;
    }

    /**
     * Returns the token in the usage message that should be highlighted.
     *
     * @return Token in the usage message to highlight.
     */
    public String getUsageToken() {
        return usageToken;
    }
}
