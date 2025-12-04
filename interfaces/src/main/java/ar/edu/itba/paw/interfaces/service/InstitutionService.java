// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface InstitutionService {
    /**
     * Find an Institution via its ID.
     * @param institutionID         The ID of the Institution to be found.
     * @return                      An Institution if it was found.
     */
    Institution findByID(long institutionID);

    /**
     * Find an Institution via its unique domain name.
     * @param domainName            The domain name of the Institution to be found.
     * @return                      An Institution if it was found.
     */
    Institution findByDomainName(String domainName);

    /**
     * Find an Institution via its unique administrator email.
     * @param adminEmail            The email that belongs to the administrator of the Institution to be found.
     * @return                      An Institution if it was found.
     */
    Institution findByAdminEmail(String adminEmail);

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
     * @param institutionID         The ID of the Institution.
     * @param pageNum              Page Number (pagination).
     * @return                      A List of Organization objects (that might be empty).
     */
    List<Organization> getOrganizationsByID(long institutionID, int pageNum);

    /**
     * Get all organizations that belong to an institution.
     * @param domainName            The domain name of the Institution.
     * @param pageNum              Page Number (pagination).
     * @return                      A List of Organization objects (that might be empty).
     */
    List<Organization> getOrganizationsByDomainName(String domainName, int pageNum);

    /**
     * Get an institution's administrator.
     * @param institutionID         The ID of the Institution.
     * @return                      The User object that represents that institution's administrator.
     */
    User getAdministratorByID(long institutionID);

    /**
     * Get an institution's administrator.
     * @param domainName            The domain name of the Institution.
     * @return                      The User object that represents that institution's administrator.
     */
    User getAdministratorByDomainName(String domainName);

    /**
     * Check if a User is an administrator of an institution.
     * @param user                  The User to check.
     * @return                      Boolean indicating whether the user is an administrator.
     */
    boolean isAdmin(User user);

    /**
     * Get all institutions.
     * @param pageNum               Page Number (pagination).
     * @return                      A List of Institution objects (that might be empty).
     */
    List<Institution> getAllInstitutions(int pageNum);

    /**
     * Check whether a domain name is already taken by an institution.
     * @param domainName            The domain name to check.
     * @return                      Boolean indicating result.
     */
    boolean existsDomainName(String domainName);
}
