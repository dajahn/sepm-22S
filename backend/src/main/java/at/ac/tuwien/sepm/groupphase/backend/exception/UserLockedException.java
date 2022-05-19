package at.ac.tuwien.sepm.groupphase.backend.exception;


/**
 * This is the exception wich is thrown when a User is locked but tries to login.
 */
public class UserLockedException  extends RuntimeException {
    public UserLockedException(String message) {
        super(message);
    }

    public UserLockedException(String message, Throwable e) {
        super(message, e);
    }
}
