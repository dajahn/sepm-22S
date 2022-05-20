package at.ac.tuwien.sepm.groupphase.backend.exception;

public class CouldNotLockUserException  extends RuntimeException {
    public CouldNotLockUserException(String message) {
        super(message);
    }

    public CouldNotLockUserException(String message, Throwable e) {
        super(message, e);
    }
}
