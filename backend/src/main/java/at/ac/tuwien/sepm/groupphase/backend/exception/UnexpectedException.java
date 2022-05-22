package at.ac.tuwien.sepm.groupphase.backend.exception;


public class UnexpectedException extends RuntimeException {

    public UnexpectedException() {
    }

    public UnexpectedException(String message) {
        super(message);
    }

    public UnexpectedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedException(Exception e) {
        super(e);
    }
}
