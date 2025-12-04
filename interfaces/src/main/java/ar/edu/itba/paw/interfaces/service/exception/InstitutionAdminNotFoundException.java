package ar.edu.itba.paw.interfaces.service.exception;

public class InstitutionAdminNotFoundException extends RuntimeException {
    public InstitutionAdminNotFoundException(final long institutionID) {
        super("Administrator user for Institution with ID " + institutionID + " not found.");
    }
    public InstitutionAdminNotFoundException(final String domainName) {
        super("Administrator user for Institution with domain name \"" + domainName + "\" not found.");
    }
}
