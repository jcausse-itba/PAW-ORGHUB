package ar.edu.itba.paw.interfaces.service.exception;

public class InstitutionNotFoundException extends RuntimeException {
    public InstitutionNotFoundException(final long institutionID) {
        super("Institution with id " + institutionID + " not found.");
    }
    public InstitutionNotFoundException(final String message) {
        super(message);
    }
}
