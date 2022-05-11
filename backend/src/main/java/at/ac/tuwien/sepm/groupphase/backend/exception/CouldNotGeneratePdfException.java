package at.ac.tuwien.sepm.groupphase.backend.exception;


public class CouldNotGeneratePdfException extends RuntimeException {

    private static final String MESSAGE = "Could not generate pdf.";

    public CouldNotGeneratePdfException() {
        super(MESSAGE);
    }

    public CouldNotGeneratePdfException(Exception e) {
        super(MESSAGE, e);
    }
}
