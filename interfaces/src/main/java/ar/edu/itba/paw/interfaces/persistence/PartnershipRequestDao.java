// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;

import java.util.List;
import java.util.Optional;

public interface PartnershipRequestDao {
    /**
     * Find a PartnershipRequest via its ID.
     * @param requestID         The ID of the PartnershipRequest to be found.
     * @return                  An Optional containing a PartnershipRequest if it was found. An empty Optional otherwise.
     */
    Optional<PartnershipRequest> findByID(long requestID);

    /**
     * Get all partnership requests.
     * @return                  A List of PartnershipRequest objects (that might be empty).
     */
    List<PartnershipRequest> findAll();

    /**
     * Get all partnership requests that have a specific status.
     * @param status            The Status to filter by.
     * @return                  A List of PartnershipRequest objects (that might be empty).
     */
    List<PartnershipRequest> findByStatus(Status status);

    /**
     * Create a new partnership request and get the PartnershipRequest object that represents it.
     * @param institutionName   The requesting institution's name. Must not be null.
     * @param domainName        The requesting institution's domain name. Must be unique and not null.
     * @param adminEmail        The requesting institution administrator's email. Must not be null.
     * @return                  A PartnershipRequest representing the created request.
     */
    PartnershipRequest create(String institutionName, String domainName, String adminEmail);

    /**
     * Update the status of a partnership request.
     * @param pr                The PartnershipRequest to update.
     * @param status            The new Status to assign.
     */
    void updateStatus(PartnershipRequest pr, Status status);

    /**
     * Check whether a domain name is already taken by a partnership request or not.
     * @param domainName        The domain name to check.
     * @return                  Boolean indicating result.
     */
    boolean existsByDomainName(String domainName);
}
