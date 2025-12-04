package ar.edu.itba.paw.interfaces.service.exception;

public class OpenPositionNotFoundException extends RuntimeException {
    public OpenPositionNotFoundException(final long positionID) {
        super("Open position with ID " + positionID + " not found.");
    }
}
