package ar.edu.itba.paw.webapp.controller.org;

import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.webapp.auth.WebPermissionService;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.OrganigramForm;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.WebDataBinder;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrganigramController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganigramController.class);

    private final OrganigramService organigramService;
    private final OrganizationService organizationService;
    private final WebPermissionService webPermissionService;

    @InitBinder("organigramForm")
    public void initBinder(WebDataBinder binder, @PathVariable(value = "orgID", required = false) Long orgID) {
        if (orgID != null) {
            OrganigramForm form = (OrganigramForm) binder.getTarget();
            if (form != null) {
                form.setOrganizationID(orgID);
            }
        }
    }

    @GetMapping("/org/{orgID}/organigram")
    @PreAuthorize("@webPermissionService.canAccessOrganization(authentication, #orgID)")
    public ModelAndView viewOrganigram(@PathVariable("orgID") final long orgID,
                                       @ModelAttribute("currentUser") final Optional<User> currentUser,
                                       final Authentication authentication) {
        final ModelAndView mav = new ModelAndView("org/organigram");
        
        final List<Organigram> organigram = organigramService.getOrganigramTree(orgID);
        mav.addObject("organigram", organigram);
        mav.addObject("orgID", orgID);
        
        final Map<Long, Long> organigramToOpenPositionMap = organigramService.getOrganigramToOpenPositionMap(orgID);
        mav.addObject("organigramToOpenPositionMap", organigramToOpenPositionMap);
        
        final String organizationName = organizationService.findByID(orgID).getOrgName();
        mav.addObject("organizationName", organizationName);
        
        final boolean userCanManageOrganization = webPermissionService.canManageOrganization(authentication, orgID);
        mav.addObject("userCanManageOrganization", userCanManageOrganization);
        
        final boolean isSystemAdministrator = webPermissionService.isSystemAdministrator(authentication);
        mav.addObject("isSystemAdministrator", isSystemAdministrator);
        
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        
        return mav;
    }

    @GetMapping("/org/{orgID}/organigram/create")
    @PreAuthorize("@webPermissionService.canManageOrganization(authentication, #orgID)")
    public ModelAndView createPositionForm(@PathVariable("orgID") final long orgID,
                                           @ModelAttribute("currentUser") final Optional<User> currentUser) {
        final ModelAndView mav = new ModelAndView("org/organigram-create");
        
        final List<Organigram> organigram = organigramService.getOrganigramTree(orgID);
        mav.addObject("organigram", organigram);
        mav.addObject("organigramForm", new OrganigramForm());
        mav.addObject("orgID", orgID);
        
        final String organizationName = organizationService.findByID(orgID).getOrgName();
        mav.addObject("organizationName", organizationName);
        
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        
        return mav;
    }


    @PostMapping("/org/{orgID}/organigram/create")
    @PreAuthorize("@webPermissionService.canManageOrganization(authentication, #orgID)")
    public ModelAndView createPosition(@PathVariable("orgID") final long orgID,
                                       @Valid @ModelAttribute("organigramForm") final OrganigramForm form,
                                       final BindingResult bindingResult
                                       /*final HttpServletRequest request*/) {
        if (bindingResult.hasErrors()) {
            final ModelAndView mav = new ModelAndView("org/organigram-create");
            final List<Organigram> organigram = organigramService.getOrganigramTree(orgID);
            mav.addObject("organigram", organigram);
            mav.addObject("orgID", orgID);
            
            final String organizationName = organizationService.findByID(orgID).getOrgName();
            mav.addObject("organizationName", organizationName);
            
            return mav;
        }

        final Organigram createdPosition = organigramService.createPosition(
            orgID,
            form.getPositionName(),
            form.getDescription(),
            form.getParentPositionID(),
            form.getSortOrder(),
            form.getAssignedUserEmail()
        );
        
        LOGGER.info("Created new organigram position: {} for organization: {}", form.getPositionName(), orgID);
        return new ModelAndView("redirect:/org/" + orgID + "/organigram");
    }

}
