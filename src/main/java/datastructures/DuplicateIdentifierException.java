package datastructures;

public class DuplicateIdentifierException extends Exception {

    /**
     * Constructs a new DuplicateIdentifierException with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DuplicateIdentifierException(String message) {
        super(message);
    }
}
