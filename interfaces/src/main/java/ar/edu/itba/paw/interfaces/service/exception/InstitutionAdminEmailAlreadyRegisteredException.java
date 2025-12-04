package ar.edu.itba.paw.interfaces.service.exception;

public class InstitutionAdminEmailAlreadyRegisteredException extends RuntimeException {
    public InstitutionAdminEmailAlreadyRegisteredException(final String adminEmail) {
        super("The email \"" + adminEmail + "\" is already associated to another institution.");
    }
}
