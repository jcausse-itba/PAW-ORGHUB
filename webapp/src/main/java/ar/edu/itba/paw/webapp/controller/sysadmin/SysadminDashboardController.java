package ar.edu.itba.paw.webapp.controller.sysadmin;

import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.PartnershipRequestService;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.PartnershipRequest;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sysadmin")
@RequiredArgsConstructor
public class SysadminDashboardController {
    private final InstitutionService institutionService;
    private final OrganizationService organizationService;
    private final PartnershipRequestService partnershipRequestService;

    @GetMapping
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView dashboard(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        List<Institution> institutions = institutionService.getAllInstitutions(0);
        List<Organization> organizations = organizationService.getAllOrganizations(0);
        List<PartnershipRequest> allRequests = partnershipRequestService.getAllRequests();
        
        long pendingRequests = allRequests.stream()
                .mapToLong(r -> r.getStatus() == Status.PENDING ? 1 : 0)
                .sum();
        long acceptedRequests = allRequests.stream()
                .mapToLong(r -> r.getStatus() == Status.ACCEPTED ? 1 : 0)
                .sum();

        ModelAndView mav = new ModelAndView("sysadmin/dashboard");
        mav.addObject("totalInstitutions", institutions.size());
        mav.addObject("totalOrganizations", organizations.size());
        mav.addObject("pendingRequests", pendingRequests);
        mav.addObject("acceptedRequests", acceptedRequests);
        mav.addObject("userRole", "SYSTEM_ADMIN");
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        return mav;
    }
}
