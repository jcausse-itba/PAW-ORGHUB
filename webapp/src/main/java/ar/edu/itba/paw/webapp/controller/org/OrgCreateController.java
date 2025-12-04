package ar.edu.itba.paw.webapp.controller.org;

import ar.edu.itba.paw.interfaces.service.OrganizationService;
import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.OrgForm;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class OrgCreateController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgCreateController.class);

    private final OrganizationService orgService;
    private final UserService userService;

    @GetMapping("/org/create")
    @PreAuthorize("@webPermissionService.canCreateOrganizations(authentication)")
    public ModelAndView orgCreateGET(@ModelAttribute("currentUser") Optional<User> currentUser) {
        LOGGER.debug("Organization creation form accessed. User: {}.", 
                currentUser.map(User::getUserEmail).orElse("anonymous"));
        
        ModelAndView mav = new ModelAndView("/org/create");
        mav.addObject("orgForm", new OrgForm());
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/org/create")
    @PreAuthorize("@webPermissionService.canCreateOrganizations(authentication)")
    public ModelAndView orgCreatePOST(
            @Valid @ModelAttribute final OrgForm form,
            BindingResult br,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        if (br.hasErrors()) {
            return new ModelAndView("/org/create", "orgForm", form);
        }
        
        LOGGER.info("Creating organization '{}' with email '{}' by user {}.", 
                form.getOrgName(), form.getOrgEmail(), 
                currentUser.map(User::getUserEmail).orElse("unknown"));
        
        try {
            Organization org = orgService.create(
                    form.getOrgEmail(),
                    form.getOrgName(),
                    form.getSocialLink1(),
                    form.getSocialLink2(),
                    form.getDescription(),
                    currentUser.orElseThrow(() -> new IllegalArgumentException("Could not get current user")).getInstitution().getInstitutionID()
            );
            userService.assignRoleForOrganizationByID(userService.findByEmail(form.getManagerEmail()).getUserID(), org.getOrgID(), Role.MANAGER);
            
            LOGGER.info("Organization '{}' created successfully with ID {}.", form.getOrgName(), org.getOrgID());
            return new ModelAndView("redirect:/org/" + org.getOrgID() + "/profile");
        } catch (RuntimeException e) {
            LOGGER.error("Error creating organization '{}': {}.", form.getOrgName(), e.getMessage(), e);
            throw e;
        }
    }
}
