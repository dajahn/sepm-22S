package at.ac.tuwien.sepm.groupphase.backend.exception;

/**
 * This is the exception which is thrown when a User input is not Valid.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable e) {
        super(message, e);
    }
}
