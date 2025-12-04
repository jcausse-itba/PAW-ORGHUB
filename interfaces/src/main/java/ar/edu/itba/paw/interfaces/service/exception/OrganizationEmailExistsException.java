package ar.edu.itba.paw.interfaces.service.exception;

public class OrganizationEmailExistsException extends RuntimeException {
    public OrganizationEmailExistsException(final String orgEmail) {
        super("Organization with email \"" + orgEmail + "\" already exists.");
    }
}
