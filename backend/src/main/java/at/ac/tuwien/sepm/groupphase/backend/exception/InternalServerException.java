package at.ac.tuwien.sepm.groupphase.backend.exception;


public class InternalServerException extends RuntimeException {

    public InternalServerException() {
    }

    public InternalServerException(String message) {
        super(message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalServerException(Exception e) {
        super(e);
    }
}
