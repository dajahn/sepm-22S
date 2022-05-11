package at.ac.tuwien.sepm.groupphase.backend.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable e) {
        super(message, e);
    }
}
