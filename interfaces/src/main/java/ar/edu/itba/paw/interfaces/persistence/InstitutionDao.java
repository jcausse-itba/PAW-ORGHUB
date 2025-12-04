// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Organization;

import java.util.List;
import java.util.Optional;

public interface InstitutionDao {
    /**
     * * Find an Institution via its ID.
     * @param institutionID         The ID of the Institution to find.
     * @return                      An Optional containing an Institution if it was found. An empty Optional otherwise.
     */
    Optional<Institution> findByID(long institutionID);

    /**
     * * Find an Institution via its unique domain name.
     * @param domainName            The domain name of the Institution to find.
     * @return                      An Optional containing an Institution if it was found. An empty Optional otherwise.
     */
    Optional<Institution> findByDomainName(String domainName);

    /**
     * * Find an Institution via its unique administrator email.
     * @param adminEmail            The email that belongs to the administrator of the Institution to find.
     * @return                      An Optional containing an Institution if it was found. An empty Optional otherwise.
     */
    Optional<Institution> findByAdminEmail(String adminEmail);

    /**
     * Get all institutions
     * @param pageNum               Page Number (pagination)
     * @return                      A list of Institutions.
     */
    List<Institution> findAll(int pageNum);

    /**
     * Create a new institution and get the Institution object that represents it.
     * @param name                  The institution's name. Must not be null.
     * @param domainName            The institution's domain name. Must be unique and not null.
     * @param adminEmail            The institution administrator's email. Must be unique and not null.
     * @return                      An Institution representing the created institution.
     */
    Institution create(String name, String domainName, String adminEmail);

    /**
     * Get all organizations that belong to an institution.
     * @param inst                  The Institution.
     * @param pageNum               Page Number (pagination)
     * @return                      A List of Organization objects (that might be empty).
     */
    List<Organization> getOrganizations(Institution inst, int pageNum);

    /**
     * Get an institution's administrator.
     * @param inst                  The Institution
     * @return                      The User object that represents that institution's administrator.
     */
    User getAdministrator(Institution inst);

    /**
     * Check whether a domain name is already taken by an institution.
     * @param domainName            The domain name to check.
     * @return                      Boolean indicating result
     */
    boolean existsDomainName(String domainName);

    /**
     * Check whether an email is already taken by the administrator of an institution.
     * @param adminEmail            The email to check.
     * @return                      Boolean indicating result
     */
    boolean existsAdminEmail(String adminEmail);
}
