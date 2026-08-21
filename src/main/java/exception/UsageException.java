package exception;

/** Describes an invalid command field and the format expected by the command. */
public class UsageException extends Exception {
    private final String action;
    private final String fieldName;
    private final String actualValue;
    private final String expectedType;
    private final String usageMessage;
    private final String usageToken;

    public UsageException(String action, String fieldName, String actualValue,
                          String expectedType, String usageMessage, String usageToken) {
        this(action, fieldName, actualValue, expectedType, usageMessage, usageToken, null);
    }

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

    public String getAction() {
        return action;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getActualValue() {
        return actualValue;
    }

    public String getExpectedType() {
        return expectedType;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public String getUsageToken() {
        return usageToken;
    }
}
