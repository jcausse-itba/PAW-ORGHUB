package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.service.*;
import ar.edu.itba.paw.interfaces.service.exception.EventNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.OrganizationNotFoundException;
import ar.edu.itba.paw.interfaces.service.exception.PostNotFoundException;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {
    private final UserService userService;
    private final InstitutionService institutionService;
    private final OrganizationService organizationService;
    private final PostService postService;
    private final EventService eventService;
    private final ImageService imageService;

    @Override
    @TransactionalRead
    public boolean canAccessOrganization(final User user, final long organizationID) {
        long orgInstID;
        try {
            orgInstID = organizationService.getInstitutionIDByID(organizationID);
        } catch (OrganizationNotFoundException e) {
            return false;
        }
        return user.getInstitution().getInstitutionID() == orgInstID;
    }

    @Override
    @TransactionalRead
    public boolean canAccessPost(final User user, final long postID) {
        final long postOrgInstID;
        try {
            final long postOrgID = postService.getOrganizationIDByID(postID);
            postOrgInstID = organizationService.getInstitutionIDByID(postOrgID);
        } catch (PostNotFoundException | OrganizationNotFoundException e) {
            return false;
        }
        return user.getInstitution().getInstitutionID() == postOrgInstID;
    }

    @Override
    @TransactionalRead
    public boolean canAccessEvent(final User user, final long eventID) {
        final long eventOrgInstID;
        try {
            final long eventOrgID = eventService.getOrganizationIDByID(eventID);
            eventOrgInstID = organizationService.getInstitutionIDByID(eventOrgID);
        } catch (EventNotFoundException | OrganizationNotFoundException e) {
            return false;
        }
        return user.getInstitution().getInstitutionID() == eventOrgInstID;
    }

    @Override
    @TransactionalRead
    public boolean canAccessOpenPosition(final User user, final long organizationID, final long positionID) {
        return canAccessOrganization(user, organizationID);
    }

    @Override
    @TransactionalRead
    public boolean canAccessImage(final User user, final int imageID) {
        return imageService.getImage(imageID)
                .map(image -> userService.canUserAccessImage(user, image))
                .orElse(false);
    }

    @Override
    @TransactionalRead
    public boolean canCreateOrganizations(final User user) {
        return isInstitutionAdministrator(user);
    }

    @Override
    @TransactionalRead
    public boolean canManageOrganization(final User user, final long organizationID){
        return isOrganizationManager(user, organizationID);
    }

    @Override
    @TransactionalRead
    public boolean canCreateOpenPositions(final User user, final long organizationID) {
        return canManageOrganization(user, organizationID);
    }

    @Override
    @TransactionalRead
    public boolean canManageOpenPositions(final User user, final long orgID) {
        return canManageOrganization(user, orgID);
    }

    @Override
    @TransactionalRead
    public boolean canPublish(final User user, final long organizationID) {
        return isOrganizationPublisher(user, organizationID) || isOrganizationManager(user, organizationID);
    }

    @Override
    @TransactionalRead
    public boolean canManageEvents(final User user, final long organizationID) {
        return canManageOrganization(user, organizationID);
    }

    @Override
    @TransactionalRead
    public boolean canEnroll(final User user, final long eventID) {
        return canAccessEvent(user, eventID);
    }

    @Override
    @TransactionalRead
    public boolean canUnenroll(final User user, final long eventID) {
        return canAccessEvent(user, eventID);
    }

    @Override
    @TransactionalRead
    public boolean canApplyOpenPosition(final User user, final long organizationID, final long positionID) {
        return canAccessOpenPosition(user, organizationID, positionID);
    }

    @Override
    @TransactionalRead
    public boolean canWithdrawOpenPosition(final User user, final long organizationID, final long positionID) {
        return canAccessOpenPosition(user, organizationID, positionID);
    }

    private boolean userRoleEquals(final User user, final long organizationID, final Role role) {
        return userService.getRoleForOrganizationByID(user.getUserID(), organizationID).equals(role);
    }

    @Override
    @TransactionalRead
    public boolean isOrganizationPublisher(final User user, final long organizationID) {
        return userRoleEquals(user, organizationID, Role.PUBLISHER);
    }

    @Override
    @TransactionalRead
    public boolean isOrganizationManager(final User user, final long organizationID) {
        return userRoleEquals(user, organizationID, Role.MANAGER);
    }

    @Override
    @TransactionalRead
    public boolean isInstitutionAdministrator(final User user) {
        return institutionService.isAdmin(user);
    }

    @Override
    @TransactionalRead
    public boolean isSystemAdministrator(final User user) {
        return user.getUserID() == SYSADMIN_USER_ID;
    }
}
