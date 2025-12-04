package ar.edu.itba.paw.webapp.controller.sysadmin;

import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/sysadmin/institutions")
@RequiredArgsConstructor
public class SysadminInstitutionController {
    private final InstitutionService institutionService;
    private final OrganizationService organizationService;

    @GetMapping
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView manageInstitutions(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        List<Institution> institutions = institutionService.getAllInstitutions(0);

        ModelAndView mav = new ModelAndView("sysadmin/institutions");
        mav.addObject("institutions", institutions);
        mav.addObject("totalCount", institutions.size());
        mav.addObject("userRole", "SYSTEM_ADMIN");
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        return mav;
    }

    @GetMapping("/{institutionID}/organizations")
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView viewInstitutionOrganizations(
            @PathVariable("institutionID") long institutionID,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        Institution institution = institutionService.findByID(institutionID);
        if (institution == null) {
            return new ModelAndView("redirect:/sysadmin/institutions");
        }

        List<Organization> organizations = organizationService.findAllForInstitution(institutionID, 0);

        ModelAndView mav = new ModelAndView("sysadmin/institution-organizations");
        mav.addObject("institution", institution);
        mav.addObject("organizations", organizations);
        mav.addObject("totalCount", organizations.size());
        mav.addObject("userRole", "SYSTEM_ADMIN");
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));

        return mav;
    }
}
