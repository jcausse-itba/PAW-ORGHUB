package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.PermissionService;
import ar.edu.itba.paw.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
public class RootController {
    private final PermissionService permissionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @RequestMapping(path = "/", method = RequestMethod.GET)
    @PreAuthorize("permitAll()")
    public ModelAndView rootRedirect(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        if  (currentUser.isPresent()) {
            if (permissionService.isSystemAdministrator(currentUser.get())) {
                LOGGER.debug("User {} is System Administrator. Redirecting to sysadmin dashboard.",  currentUser.get().getUserEmail());
                return new ModelAndView("redirect:/sysadmin");
            }

            LOGGER.debug("User {} is not System Administrator. Redirecting to home page.",  currentUser.get().getUserEmail());
            return new ModelAndView("redirect:/home");
        }

        LOGGER.debug("User not logged in. Redirecting to landing page.");
        return new ModelAndView( "redirect:/landing" );
    }
}
