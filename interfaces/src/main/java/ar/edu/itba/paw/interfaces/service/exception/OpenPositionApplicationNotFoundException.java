package ar.edu.itba.paw.interfaces.service.exception;

public class OpenPositionApplicationNotFoundException extends RuntimeException {
    public OpenPositionApplicationNotFoundException(final long applicationID) {
        super("OpenPositionApplication with ID " + applicationID + " not found.");
    }
    public OpenPositionApplicationNotFoundException(final String message) {
        super(message);
    }
}
