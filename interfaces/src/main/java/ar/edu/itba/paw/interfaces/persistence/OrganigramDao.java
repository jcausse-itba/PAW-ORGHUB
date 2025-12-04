// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;

import java.util.List;
import java.util.Optional;

public interface OrganigramDao {
    /**
     * Find an Organigram position via its ID.
     * @param organigramID        The ID of the Organigram to be found.
     * @return                    An Optional containing an Organigram if it was found. An empty Optional otherwise.
     */
    Optional<Organigram> findByID(long organigramID);
    
    /**
     * Find all Organigram positions for an organization.
     * @param organization        The Organization. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findByOrganization(Organization organization);
    
    /**
     * Find all Organigram positions for an organization at a specific level.
     * @param organization        The Organization. Must not be null.
     * @param level               The level.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findByOrganizationAndLevel(Organization organization, int level);
    
    /**
     * Find all root positions (positions without parent) for an organization.
     * @param organization        The Organization. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findRootPositionsByOrganization(Organization organization);
    
    /**
     * Find all child positions of a parent position.
     * @param parentPosition      The parent Organigram position. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findChildPositions(Organigram parentPosition);
    
    /**
     * Create a new Organigram position.
     * @param organization        The Organization. Must not be null.
     * @param positionName        The position name. Must not be null.
     * @param description         The description. Must not be null.
     * @param parentPosition      The parent Organigram position (optional).
     * @param level               The level.
     * @param sortOrder           The sort order.
     * @return                    An Organigram representing the created position.
     */
    Organigram create(Organization organization, String positionName, String description, Organigram parentPosition, int level, int sortOrder);
    
    /**
     * Update an Organigram position.
     * @param position            The Organigram to update. Must not be null.
     * @param positionName        The new position name.
     * @param description         The new description.
     * @param parentPosition      The new parent Organigram position (optional).
     * @param level               The new level.
     * @param sortOrder           The new sort order.
     */
    void updatePosition(Organigram position, String positionName, String description, Organigram parentPosition, int level, int sortOrder);
    
    /**
     * Delete an Organigram position.
     * @param position            The Organigram to delete. Must not be null.
     */
    void delete(Organigram position);
    
    /**
     * Check if a position name exists in an organization.
     * @param positionName        The position name to check.
     * @param organization        The Organization. Must not be null.
     * @return                    Boolean indicating result.
     */
    boolean existsPositionNameInOrganization(String positionName, Organization organization);
    
    /**
     * Check if a position has child positions.
     * @param position            The Organigram position. Must not be null.
     * @return                    Boolean indicating result.
     */
    boolean hasChildPositions(Organigram position);
    
    /**
     * Check if a sort order exists in a level for an organization.
     * @param level               The level.
     * @param sortOrder           The sort order.
     * @param organization        The Organization. Must not be null.
     * @return                    Boolean indicating result.
     */
    boolean existsSortOrderInLevel(int level, int sortOrder, Organization organization);
    
    /**
     * Check if a sort order exists in a parent position for an organization.
     * @param parentPosition      The parent Organigram position (optional).
     * @param sortOrder           The sort order.
     * @param organization        The Organization. Must not be null.
     * @return                    Boolean indicating result.
     */
    boolean existsSortOrderInParent(Organigram parentPosition, int sortOrder, Organization organization);
    
    /**
     * Assign a user to an Organigram position.
     * @param position            The Organigram position. Must not be null.
     * @param user                The User to assign. Must not be null.
     */
    void assignUserToPosition(Organigram position, User user);
    
    /**
     * Unassign a user from an Organigram position.
     * @param position            The Organigram position. Must not be null.
     */
    void unassignUserFromPosition(Organigram position);
    
    /**
     * Find all Organigram positions assigned to a user.
     * @param user                The User. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findByUser(User user);
    
    /**
     * Find all Organigram positions assigned to a user in a specific organization.
     * @param user                The User. Must not be null.
     * @param organization        The Organization. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findByUserAndOrganization(User user, Organization organization);
    
    /**
     * Find all vacant positions (without assigned user and not used by active open positions) for an organization.
     * @param organization        The Organization. Must not be null.
     * @return                    A List of Organigram objects (that might be empty).
     */
    List<Organigram> findVacantPositionsByOrganization(Organization organization);
}
