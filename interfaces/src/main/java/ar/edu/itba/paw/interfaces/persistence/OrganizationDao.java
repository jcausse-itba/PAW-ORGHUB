// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.*;

import java.util.List;
import java.util.Optional;

public interface OrganizationDao {
    /**
     * Find an Organization via its ID.
     * @param orgID             The ID of the Organization to be found.
     * @return                  An Optional containing an Organization if it was found. An empty Optional otherwise.
     */
    Optional<Organization> findByID(long orgID);

    /**
     * Find an Organization via its unique email address.
     * @param email             The email of the Organization to be found.
     * @return                  An Optional containing an Organization if it was found. An empty Optional otherwise.
     */
    Optional<Organization> findByEmail(String email);

    /**
     * Get all organizations that belong to a specific institution.
     * @param inst              The Institution.
     * @param pageNum           Page Number (pagination)
     * @return                  A List of Organization objects (that might be empty).
     */
    List<Organization> findAllForInstitution(Institution inst, int pageNum);

    /**
     * Get all organizations.
     * @param pageNum           Page Number (pagination)
     * @return                  A List of Organization objects (that might be empty).
     */
    List<Organization> findAll(int pageNum);

    /**
     * Create a new organization and get the Organization object that represents it.
     * @param email             Organization's email. Must be unique and not null.
     * @param name              Organization's name. Must not be null.
     * @param social1           Organization's first social media link (e.g., Instagram).
     * @param social2           Organization's second social media link (e.g., Twitter/X).
     * @param desc              Organization's description.
     * @param inst              The Institution the organization belongs to. Must not be null.
     * @return                  An Organization representing the created organization.
     */
    Organization create(String email, String name, String social1, String social2, String desc, Institution inst);

    /**
     * Get all users that have Publisher role for an organization.
     * @param org               The Organization.
     * @return                  A List of User objects (that might be empty).
     */
    List<User> getPublishers(Organization org);

    /**
     * Get all users that have Manager (administrator) role for an organization.
     * @param org               The Organization.
     * @return                  A List of User objects (that might be empty).
     */
    List<User> getManagers(Organization org);

    /**
     * Check whether an email is already taken by an organization or not.
     * @param email             The email address to check.
     * @return                  Boolean indicating result.
     */
    boolean existsEmail(String email);
}
