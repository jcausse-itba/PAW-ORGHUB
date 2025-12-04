package ar.edu.itba.paw.webapp.controller.institution;

import ar.edu.itba.paw.interfaces.service.InstitutionService;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.form.InstitutionRegistrationForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class InstCreateController {
    private final InstitutionService institutionService;

    @GetMapping("/sysadmin/institution/create")
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView newInstitutionGET(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        final ModelAndView mav = new ModelAndView("institution/create");
        mav.addObject("institutionForm", new InstitutionRegistrationForm());
        mav.addObject("userRole", "SYSTEM_ADMIN");
        currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
        return mav;
    }

    @PostMapping("/sysadmin/institution/create")
    @PreAuthorize("@webPermissionService.isSystemAdministrator(authentication)")
    public ModelAndView newInstitutionPOST(
            @Valid @ModelAttribute final InstitutionRegistrationForm form,
            BindingResult br,
            @ModelAttribute("currentUser") Optional<User> currentUser
    ){
        if (br.hasErrors()) {
            ModelAndView mav = new ModelAndView("institution/create");
            mav.addObject("institutionForm", form);
            mav.addObject("userRole", "SYSTEM_ADMIN");
            currentUser.ifPresent(u -> mav.addObject("userFirstName", u.getFirstName()));
            mav.addObject("org.springframework.validation.BindingResult.institutionForm", br);
            return mav;
        }
        institutionService.create(
                form.getName(),
                form.getDomainName(),
                form.getAdminEmail()
        );
        return new ModelAndView("redirect:/sysadmin/institutions");
    }
}
