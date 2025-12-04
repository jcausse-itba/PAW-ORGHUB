package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;

import java.util.List;

public interface PartnershipRequestService {
    /**
     * Create a new partnership request.
     * @param institutionName   The requesting institution's name. Must not be null.
     * @param domainName        The requesting institution's domain name. Must be unique and not null.
     * @param adminEmail        The requesting institution administrator's email. Must not be null.
     * @return                  A PartnershipRequest representing the created request.
     */
    PartnershipRequest createRequest(String institutionName, String domainName, String adminEmail);

    /**
     * Get all partnership requests.
     * @return                  A List of PartnershipRequest objects (that might be empty).
     */
    List<PartnershipRequest> getAllRequests();

    /**
     * Get all partnership requests that have a specific status.
     * @param status            The Status to filter by.
     * @return                  A List of PartnershipRequest objects (that might be empty).
     */
    List<PartnershipRequest> getRequestsByStatus(Status status);

    /**
     * Find a PartnershipRequest via its ID.
     * @param requestID         The ID of the PartnershipRequest to be found.
     * @return                  A PartnershipRequest if it was found.
     * @throws                  PartnershipRequestNotFoundException if the request was not found.
     */
    PartnershipRequest getRequestByID(long requestID);

    /**
     * Approve a partnership request and create the corresponding institution.
     * @param requestID         The ID of the PartnershipRequest to approve.
     */
    void approveRequest(long requestID);

    /**
     * Reject a partnership request.
     * @param requestID         The ID of the PartnershipRequest to reject.
     */
    void rejectRequest(long requestID);

    /**
     * Check whether a domain name is already taken by a partnership request or not.
     * @param domainName        The domain name to check.
     * @return                  Boolean indicating result.
     */
    boolean domainNameExists(String domainName);
}
