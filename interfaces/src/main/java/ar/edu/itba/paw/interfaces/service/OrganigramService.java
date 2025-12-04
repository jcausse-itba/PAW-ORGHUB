// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.Organigram;

import java.util.List;
import java.util.Optional;

public interface OrganigramService {
    /**
     * Find an Organigram position via its ID.
     * @param organigramID            The ID of the Organigram to be found.
     * @return                        An Optional containing an Organigram if it was found. An empty Optional otherwise.
     */
    Optional<Organigram> findByID(long organigramID);

    /**
     * Find all Organigram positions for an organization.
     * @param organizationID          The ID of the Organization.
     * @return                        A List of Organigram objects (that might be empty).
     */
    List<Organigram> findByOrganizationID(long organizationID);

    /**
     * Find all root positions (positions without parent) for an organization.
     * @param organizationID          The ID of the Organization.
     * @return                        A List of Organigram objects (that might be empty).
     */
    List<Organigram> findRootPositionsByOrganizationID(long organizationID);

    /**
     * Find all child positions of a parent position.
     * @param parentPositionID        The ID of the parent Organigram position.
     * @return                        A List of Organigram objects (that might be empty).
     */
    List<Organigram> findChildPositions(long parentPositionID);

    /**
     * Create a new Organigram position and get the Organigram object that represents it.
     * The level is calculated automatically: 0 if root, parent.getLevel() + 1 if has parent.
     * @param organizationID          The ID of the Organization. Must not be null.
     * @param positionName            The position name. Must not be null.
     * @param description             The description. Must not be null.
     * @param parentPositionID        The ID of the parent Organigram position (optional).
     * @param sortOrder               The sort order.
     * @param assignedUserEmail       The email of the user to assign to this position (optional).
     * @return                        An Organigram representing the created position.
     */
    Organigram createPosition(long organizationID, String positionName, String description, Long parentPositionID, int sortOrder, String assignedUserEmail);

    /**
     * Update an Organigram position.
     * The level is calculated automatically: 0 if root, parent.getLevel() + 1 if has parent.
     * @param organigramID            The ID of the Organigram position to update.
     * @param positionName            The new position name.
     * @param description             The new description.
     * @param parentPositionID        The ID of the new parent Organigram position (optional).
     * @param sortOrder               The new sort order.
     * @param assignedUserEmail       The email of the user to assign to this position (optional, null to unassign).
     */
    void updatePosition(long organigramID, String positionName, String description, Long parentPositionID, int sortOrder, String assignedUserEmail);

    /**
     * Delete an Organigram position.
     * @param organigramID            The ID of the Organigram position to delete.
     */
    void deletePosition(long organigramID);

    /**
     * Check if a position name exists in an organization.
     * @param positionName            The position name to check.
     * @param organizationID          The ID of the Organization.
     * @return                        Boolean indicating result.
     */
    boolean existsPositionNameInOrganization(String positionName, long organizationID);

    /**
     * Check if a position has child positions.
     * @param organigramID            The ID of the Organigram position.
     * @return                        Boolean indicating result.
     */
    boolean hasChildPositions(long organigramID);

    /**
     * Check if a sort order exists in a level for an organization.
     * @param level                   The level.
     * @param sortOrder               The sort order.
     * @param organizationID          The ID of the Organization.
     * @return                        Boolean indicating result.
     */
    boolean existsSortOrderInLevel(int level, int sortOrder, long organizationID);

    /**
     * Check if a sort order exists in a parent position for an organization.
     * @param parentPositionID        The ID of the parent Organigram position (optional).
     * @param sortOrder               The sort order.
     * @param organizationID          The ID of the Organization.
     * @return                        Boolean indicating result.
     */
    boolean existsSortOrderInParent(Long parentPositionID, int sortOrder, long organizationID);

    /**
     * Get all Organigram positions for an organization (organigram tree structure).
     * @param organizationID          The ID of the Organization.
     * @return                        A List of Organigram objects (that might be empty).
     */
    List<Organigram> getOrganigramTree(long organizationID);

    /**
     * Get all vacant positions (without assigned user) for an organization.
     * @param organizationID          The ID of the Organization.
     * @return                        A List of Organigram objects (that might be empty).
     */
    List<Organigram> getVacantPositions(long organizationID);

    /**
     * Assign a user to an Organigram position.
     * @param organigramID            The ID of the Organigram position.
     * @param userID                  The ID of the User to assign.
     */
    void assignUserToPosition(long organigramID, long userID);

    /**
     * Unassign a user from an Organigram position.
     * @param organigramID            The ID of the Organigram position.
     */
    void unassignUserFromPosition(long organigramID);

    /**
     * Get a map of organigram positions to their associated active open positions.
     * Only includes vacant positions (without assigned user) that have an active open position.
     * @param organizationID          The ID of the Organization.
     * @return                        A Map with organigramID as key and openPositionID as value.
     */
    java.util.Map<Long, Long> getOrganigramToOpenPositionMap(long organizationID);
}
