/**
 * Created by jackli on 2015-10-18.
 * <p>
 * Class of String constants for Quibble prompts, commands, messages, errors
 */
public class Constants
{
    // This error prefix should be used before all error messages
    public static final String ERROR_PREFIX = "Error: ";

    // Error messages
    public static final String ERROR_READ_FILE = ERROR_PREFIX +
            "Problem reading file";
    public static final String ERROR_EVENT_ALREADY_EXISTS = ERROR_PREFIX +
            "Event already exists";
    public static final String ERROR_INSUFFICIENT_TICKETS =
            ERROR_PREFIX + "Insufficient tickets available";

    // Back Office specific errors
    // Back Office prefix
    public static final String BACK_OFFICE = "Back Office ";
    public static final String ERROR_EVENT_DATE_PAST = ERROR_PREFIX + "Event " +
            "date is in the past.";
}

