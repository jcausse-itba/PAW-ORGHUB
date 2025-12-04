package ar.edu.itba.paw.interfaces.service.exception;

public class OrganizationNotFoundException extends RuntimeException {
    public OrganizationNotFoundException(final long orgID) {
        super("Organization with ID " + orgID + " not found.");
    }
    public OrganizationNotFoundException(final String email) {
        super("Organization with email \"" + email + "\" not found.");
    }
}
