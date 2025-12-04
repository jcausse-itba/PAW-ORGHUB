package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.auth.AuthUserDetails;
import ar.edu.itba.paw.webapp.auth.WebPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
public class UserControllerAdvice {
    private final WebPermissionService webPermissionService;
    private final OrganizationService organizationService;
    private final PermissionService permissionService;

    @ModelAttribute("currentUser")
    public Optional<User> getCurrentUser() {
        Object maybeUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (maybeUser instanceof AuthUserDetails user) {
            return Optional.of(user.getDomainUser());
        }
        return Optional.empty();
    }

    @ModelAttribute("canCreateOrganizations")
    public boolean canCreateOrganizations() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return webPermissionService.canCreateOrganizations(authentication);
    }

    @ModelAttribute("userRole")
    public String getUserRole() {
        Optional<User> currentUser = getCurrentUser();
        if (currentUser.isPresent() && permissionService.isSystemAdministrator(currentUser.get())) {
            return "SYSTEM_ADMIN";
        }
        return "USER";
    }

    @ModelAttribute("managedOrganizations")
    public List<Organization> getManagedOrganizations() {
        Object maybeUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (maybeUser instanceof AuthUserDetails authUser) {
            User user = authUser.getDomainUser();
            if (user.getInstitution() == null) {
                return List.of();
            }
            List<Organization> allOrgs = organizationService.findAllForInstitution(user.getInstitution().getInstitutionID(), 0);
            return allOrgs.stream()
                    .filter(org -> permissionService.isOrganizationManager(user, org.getOrgID()))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
