// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.User;

public interface PermissionService {
    long SYSADMIN_USER_ID = 0L;
    String SYSADMIN_EMAIL = "sysadmin@orghub.com";

    /**
     * Returns whether the {@code user} can view the Organization with ID {@code organizationID}.
     * The User's Institution must be the same as the Organization's Institution.
     */
    boolean canAccessOrganization(User user, long organizationID);

    /**
     * Returns whether the {@code user} can view the Post with ID {@code organizationID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    boolean canAccessPost(User user, long postID);

    /**
     * Returns whether the {@code user} can view the Event with ID {@code eventID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    boolean canAccessEvent(User user, long eventID);

    /**
     * Returns whether the {@code user} can view the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     * The User's Institution must be the same as the Organization's Institution.
     */
    boolean canAccessOpenPosition(User user, long organizationID, long positionID);

    /**
     * Returns whether the {@code user} can create new Organizations.
     * The User must be the Administrator for their Institution.
     */
    boolean canCreateOrganizations(User user);

    /**
     * Returns whether the {@code user} can change settings for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    boolean canManageOrganization(User user, long organizationID);

    /**
     * Returns whether the {@code user} can create Open Positions for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    boolean canCreateOpenPositions(User user, long organizationID);

    /**
     * Returns whether the {@code user} can change settings of Open Positions for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    boolean canManageOpenPositions(User user, long orgID);

    /**
     * Returns whether the {@code user} can create Posts and Events for the Organization with ID {@code organizationID}.
     * The User must be a Publisher or a Manager of that Organization.
     */
    boolean canPublish(User user, long organizationID);

    /**
     * Returns whether the {@code user} can change settings of Events for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    boolean canManageEvents(User user, long organizationID);

    /**
     * Returns whether the {@code user} is allowed to enroll to the Event with ID {@code eventID}.
     */
    boolean canEnroll(User user, long eventID);

    /**
     * Returns whether the {@code user} is allowed to unenroll to the Event with ID {@code eventID}.
     */
    boolean canUnenroll(User user, long eventID);

    /**
     * Returns whether the {@code user} is allowed to apply to the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     */
    boolean canApplyOpenPosition(User user, long organizationID, long positionID);

    /**
     * Returns whether the {@code user} is allowed to withdraw their application to the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     */
    boolean canWithdrawOpenPosition(User user, long organizationID, long positionID);

    /**
     * Returns whether the {@code user} is a Publisher ({@link Role}{@code .PUBLISHER}) for the Organization with ID {@code organizationID}.
     */
    boolean isOrganizationPublisher(User user, long organizationID);

    /**
     * Returns whether the {@code user} is a Manager ({@link Role}{@code .MANAGER}) for the Organization with ID {@code organizationID}.
     */
    boolean isOrganizationManager(User user, long organizationID);

    /**
     * Returns whether the {@code user} is the Administrator of their Institution.
     */
    boolean isInstitutionAdministrator(User user);

    /**
     * Returns whether the {@code user} can view the Image with ID {@code imageID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    boolean canAccessImage(User user, int imageID);

    /**
     * Returns whether the {@code user} is the OrgHub System Administrator.
     */
    boolean isSystemAdministrator(User user);
}
