package ar.edu.itba.paw.interfaces.service.exception;

public class PartnershipRequestNotFoundException extends RuntimeException {
    public PartnershipRequestNotFoundException(final long requestID) {
        super("PartnershipRequest with ID " + requestID + " not found.");
    }
}

