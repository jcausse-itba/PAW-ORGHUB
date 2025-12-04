package ar.edu.itba.paw.interfaces.service.exception;

public class InstitutionDomainNameExistsException extends RuntimeException {
    public InstitutionDomainNameExistsException(final String domainName) {
        super("Institution domain name \"" + domainName + "\" already exists.");
    }
}
