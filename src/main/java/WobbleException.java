/** Represents an error caused by an invalid Wobble command. */
public class WobbleException extends Exception {
    /** Creates an exception with a message explaining how to fix the command. */
    public WobbleException(String message) {
        super(message);
    }
}
