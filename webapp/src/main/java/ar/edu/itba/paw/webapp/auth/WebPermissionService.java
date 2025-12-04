package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * This is just an Adapter Pattern for PermissionService at Service layer
 */
@Service("webPermissionService")
@RequiredArgsConstructor
public class WebPermissionService {
    private final PermissionService permissionService;

    private Optional<User> getCurrentUser(final Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthUserDetails)) {
            return Optional.empty();
        }
        return Optional.of(((AuthUserDetails) authentication.getPrincipal()).getDomainUser());
    }

    /**
     * Returns whether the currently logged-in User can view the Organization with ID {@code organizationID}.
     * The User's Institution must be the same as the Organization's Institution.
     * System administrators can access any organization.
     */
    public boolean canAccessOrganization(final Authentication authentication, final long organizationID) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isPresent() && permissionService.isSystemAdministrator(currentUser.get())) {
            return true;
        }
        return currentUser.map(u -> permissionService.canAccessOrganization(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can view the Post with ID {@code organizationID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    public boolean canAccessPost(final Authentication authentication, final long postID) {
        return getCurrentUser(authentication).map(u -> permissionService.canAccessPost(u, postID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can view the Event with ID {@code eventID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    public boolean canAccessEvent(final Authentication authentication, final long eventID) {
        return getCurrentUser(authentication).map(u -> permissionService.canAccessEvent(u, eventID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can view the Image with ID {@code imageID}.
     * The User's Institution must be the same as the publishing Organization's Institution.
     */
    public boolean canAccessImage(final Authentication authentication, final int imageID) {
        return getCurrentUser(authentication).map(u -> permissionService.canAccessImage(u, imageID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can view the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     * The User's Institution must be the same as the Organization's Institution.
     */
    public boolean canAccessOpenPosition(final Authentication authentication, final long organizationID, final long positionID) {
        return getCurrentUser(authentication).map(u -> permissionService.canAccessOpenPosition(u, organizationID, positionID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can create new Organizations.
     * The User must be the Administrator for their Institution.
     */
    public boolean canCreateOrganizations(final Authentication authentication) {
        return getCurrentUser(authentication).map(permissionService::canCreateOrganizations).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can change settings for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     * System administrators cannot manage organizations (read-only access).
     */
    public boolean canManageOrganization(final Authentication authentication, final long organizationID) {
        Optional<User> currentUser = getCurrentUser(authentication);
        if (currentUser.isPresent() && permissionService.isSystemAdministrator(currentUser.get())) {
            return false;
        }
        return currentUser.map(u -> permissionService.canManageOrganization(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can create Open Positions for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    public boolean canCreateOpenPositions(final Authentication authentication, final long organizationID) {
        return getCurrentUser(authentication).map(u -> permissionService.canCreateOpenPositions(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can change settings of Open Positions for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    public boolean canManageOpenPositions(Authentication authentication, final long orgID) {
        return getCurrentUser(authentication).map(u -> permissionService.canManageOpenPositions(u, orgID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can create Posts and Events for the Organization with ID {@code organizationID}.
     * The User must be a Publisher or a Manager of that Organization.
     */
    public boolean canPublish(final Authentication authentication, final long organizationID) {
        return getCurrentUser(authentication).map(u -> permissionService.canPublish(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User can change settings of Events for the Organization with ID {@code organizationID}.
     * The User must be a Manager of that Organization.
     */
    public boolean canManageEvents(final Authentication authentication, final long organizationID) {
        return getCurrentUser(authentication).map(u -> permissionService.canManageEvents(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is allowed to enroll to the Event with ID {@code eventID}.
     */
    public boolean canEnroll(final Authentication authentication, final long eventID) {
        return getCurrentUser(authentication).map(u -> permissionService.canEnroll(u, eventID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is allowed to unenroll to the Event with ID {@code eventID}.
     */
    public boolean canUnenroll(final Authentication authentication, final long eventID) {
        return getCurrentUser(authentication).map(u -> permissionService.canUnenroll(u, eventID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is allowed to apply to the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     */
    public boolean canApplyOpenPosition(final Authentication authentication, final long organizationID, final long positionID) {
        return getCurrentUser(authentication).map(u -> permissionService.canApplyOpenPosition(u, organizationID, positionID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is allowed to withdraw their application to the Open Position with ID {@code positionID}, within the Organization with ID {@code organizationID}.
     */
    public boolean canWithdrawOpenPosition(final Authentication authentication, final long organizationID, final long positionID) {
        return getCurrentUser(authentication).map(u -> permissionService.canWithdrawOpenPosition(u, organizationID, positionID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is a Publisher ({@link Role}{@code .PUBLISHER}) for the Organization with ID {@code organizationID}.
     */
    public boolean isOrganizationPublisher(final Authentication authentication, final long organizationID){
        return getCurrentUser(authentication).map(u -> permissionService.isOrganizationPublisher(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is a Manager ({@link Role}{@code .MANAGER}) for the Organization with ID {@code organizationID}.
     */
    public boolean isOrganizationManager(final Authentication authentication, final long organizationID){
        return getCurrentUser(authentication).map(u -> permissionService.isOrganizationManager(u, organizationID)).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is the Administrator of their Institution.
     */
    public boolean isInstitutionAdministrator(final Authentication authentication){
        return getCurrentUser(authentication).map(permissionService::isInstitutionAdministrator).orElse(false);
    }

    /**
     * Returns whether the currently logged-in User is the OrgHub System Administrator.
     */
    public boolean isSystemAdministrator(final Authentication authentication) {
        return getCurrentUser(authentication).map(permissionService::isSystemAdministrator).orElse(false);
    }

    public static String getCurrentUserEmail() {
        final AuthUserDetails aud = (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return aud != null ? aud.getDomainUser().getUserEmail() : "";
    }

    public static boolean shouldRedirectToSysadminDashboard(final Authentication authentication) {
        if (authentication.getPrincipal() instanceof AuthUserDetails a) {
            return a.getDomainUser().getUserID() == PermissionService.SYSADMIN_USER_ID;
        }
        return false;
    }
}
