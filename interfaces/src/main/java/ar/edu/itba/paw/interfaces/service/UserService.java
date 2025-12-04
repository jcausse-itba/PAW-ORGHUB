// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.image.Image;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.enums.Role;

import java.util.List;

public interface UserService {
    /**
     * Find a User via its ID.
     * @param userID        The ID of the User to be found.
     * @return              A User if the user was found. Null otherwise.
     */
    User findByID(long userID);

    /**
     * Find a User via its unique email address.
     * @param email         The email of the User to be found.
     * @return              A User if the user was found. Null otherwise.
     */
    User findByEmail(String email);

    /**
     * Create a new user and get the User object that represents it.
     * @param userEmail             User's email. Must be unique and not null.
     * @param firstName             User's first name. Must not be null.
     * @param lastName              User's last name. Must not be null.
     * @param password              A String representing the user's password, not encoded yet.
     * @param preferredLanguage     A Language representing the user's preferred language. Must not be null.
     * @return                      A User representing the created user.
     */
    User create(String userEmail, String firstName, String lastName, String password, Language preferredLanguage);

    /**
     * Create a new user and get the User object that represents it.
     * @param userEmail             User's email. Must be unique and not null.
     * @param firstName             User's first name. Must not be null.
     * @param lastName              User's last name. Must not be null.
     * @param password              A String representing the user's password, not encoded yet.
     * @return                      A User representing the created user.
     */
    User create(String userEmail, String firstName, String lastName, String password);

    /**
     * Check whether a user is subscribed to an organization or not.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     * @return              Boolean indicating whether the user is subscribed to the organization.
     */
    boolean isSubscribed(long userID, long orgID);

    /**
     * Add a subscription for a user to an organization.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     */
    void addSubscription(long userID, long orgID);

    /**
     * Remove a subscription for a user from an organization.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     */
    void removeSubscription(long userID, long orgID);

    /**
     * Get all organizations that a user is subscribed to.
     * @param userID        The ID of the User.
     * @return              A List of Organization objects (that might be empty).
     */
    List<Organization> getSubscribedOrganizations(long userID);

    /**
     * Get the role that a user has for a specific organization.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     * @return              A Role representing the user's role for the organization. Null if the user has no role.
     */
    Role getRoleForOrganizationByID(long userID, long orgID);

    /**
     * Assign a role to a user for a specific organization.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     * @param role          The Role to assign. Must not be null.
     */
    void assignRoleForOrganizationByID(long userID, long orgID, Role role);

    /**
     * Delete the role that a user has for a specific organization.
     * @param userID        The ID of the User.
     * @param orgID         The ID of the Organization.
     */
    void deleteRoleForOrganizationByID(long userID, long orgID);

    /**
     * Update a user's preferred language.
     * @param userID        The ID of the User.
     * @param language      The Language to set as preferred. Must not be null.
     */
    void updatePreferredLanguage(long userID, Language language);

    /**
     * Check whether an email is already taken by a user or not.
     * @param email         The email address to check.
     * @return              Boolean indicating result.
     */
    boolean existsEmail(String email);
    
    /**
     * Get all positions (Organigram entries) that a user holds in organizations.
     * @param userID        The ID of the User.
     * @return              A List of Organigram objects (that might be empty).
     */
    List<Organigram> getUserPositions(long userID);

    /**
     * Get all positions (Organigram entries) that a user holds in a specific organization.
     * @param userID            The ID of the User.
     * @param orgID             The ID of the Organization.
     * @return                  A List of Organigram objects (that might be empty).
     */
    List<Organigram> getUserPositionsInOrganization(long userID, long orgID);

    /**
     * Get all posts that a user has liked.
     * @param userID            The ID of the User.
     * @return                  A List of Post objects (that might be empty).
     */
    List<Post> findLikedPostsByUser(long userID);

    /**
     * Check whether the specified user can view the given image.
     * @param user              The User to check. Must not be null.
     * @param image             The Image to check. Must not be null.
     * @return                  Boolean indicating whether the user can access the image.
     */
    boolean canUserAccessImage(User user, Image image);
}
