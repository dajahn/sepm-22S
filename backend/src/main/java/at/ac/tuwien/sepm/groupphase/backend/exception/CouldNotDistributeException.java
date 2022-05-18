package at.ac.tuwien.sepm.groupphase.backend.exception;


public class CouldNotDistributeException extends RuntimeException {

    private static final String MESSAGE = "Could not send email.";

    public CouldNotDistributeException() {
        super(MESSAGE);
    }
    
    public CouldNotDistributeException(Exception e) {
        super(MESSAGE, e);
    }
}
