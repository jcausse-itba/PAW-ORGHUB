// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;

import java.util.List;

public interface OrganizationService {
    /**
     * Find an Organization via its ID.
     * @param orgID              The ID of the Organization to be found.
     * @return                    An Organization if it was found.
     * @throws OrganizationNotFoundException if the organization was not found.
     */
    Organization findByID(long orgID);

    /**
     * Find an Organization via its unique email address.
     * @param email              The email of the Organization to be found.
     * @return                    An Organization if it was found.
     * @throws OrganizationNotFoundException if the organization was not found.
     */
    Organization findByEmail(String email);

    /**
     * Get all organizations that belong to a specific institution.
     * @param institutionID       The ID of the Institution.
     * @param pageNum             Page Number (pagination).
     * @return                    A List of Organization objects (that might be empty).
     */
    List<Organization> findAllForInstitution(long institutionID, int pageNum);

    /**
     * Get all users that have Publisher role for an organization.
     * @param orgID               The ID of the Organization.
     * @return                    A List of User objects (that might be empty).
     */
    List<User> getPublishers(long orgID);

    /**
     * Get all users that have Manager (administrator) role for an organization.
     * @param orgID               The ID of the Organization.
     * @return                    A List of User objects (that might be empty).
     */
    List<User> getManagers(long orgID);

    /**
     * Get all users subscribed to an organization.
     * @param orgID               The ID of the Organization.
     * @return                    A List of User objects (that might be empty).
     */
    List<User> getSubscribers(long orgID);

    /**
     * Create a new organization and get the Organization object that represents it.
     * @param orgEmail            Organization's email. Must be unique and not null.
     * @param orgName             Organization's name. Must not be null.
     * @param socialLink1         Organization's first social media link (e.g., Instagram).
     * @param socialLink2         Organization's second social media link (e.g., Twitter/X).
     * @param description         Organization's description.
     * @param institutionID       The ID of the Institution the organization belongs to.
     * @return                    An Organization representing the created organization.
     */
    Organization create(String orgEmail, String orgName, String socialLink1, String socialLink2, String description, long institutionID);

    /**
     * Get all posts published by an organization.
     * @param orgID               The ID of the Organization.
     * @param pageNum             Page Number (pagination).
     * @return                    A List of Post objects (that might be empty).
     */
    List<Post> getPostsByOrgID(long orgID, int pageNum);

    /**
     * Get all events published by an organization.
     * @param orgID               The ID of the Organization.
     * @param pageNum             Page Number (pagination).
     * @return                    A List of Event objects (that might be empty).
     */
    List<Event> getEventsByOrgID(long orgID, int pageNum);

    /**
     * Get all open positions published by an organization.
     * @param orgID               The ID of the Organization.
     * @param pageNum             Page Number (pagination).
     * @return                    A List of OpenPosition objects (that might be empty).
     */
    List<OpenPosition> getOpenPositionsByOrgID(long orgID, int pageNum);

    /**
     * Get all organizations.
     * @param pageNum             Page Number (pagination).
     * @return                    A List of Organization objects (that might be empty).
     */
    List<Organization> getAllOrganizations(int pageNum);

    /**
     * Check whether an email is already taken by an organization or not.
     * @param email               The email address to check.
     * @return                    Boolean indicating result.
     */
    boolean existsEmail(String email);

    /**
     * Get the institution ID for an organization.
     * @param orgID               The ID of the Organization.
     * @return                    The institution ID.
     * @throws OrganizationNotFoundException if the organization was not found.
     */
    long getInstitutionIDByID(long orgID);
}
