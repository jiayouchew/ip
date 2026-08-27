package wobble.exceptions;

/** Represents an error caused by an invalid Wobble command. */
public class WobbleException extends Exception {
    private static final long serialVersionUID = 1L;
    /** Creates an exception with a message explaining how to fix the command. */
    public WobbleException(String message) {
        super(message);
    }
}
