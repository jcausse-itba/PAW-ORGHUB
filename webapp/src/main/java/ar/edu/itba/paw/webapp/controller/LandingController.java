package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class LandingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LandingController.class);

    @GetMapping("/landing")
    @PreAuthorize("permitAll()")
    public ModelAndView landingGET(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        LOGGER.debug("Landing page accessed. User: {}.", 
                currentUser.map(User::getUserEmail).orElse("anonymous"));
        
        if (currentUser.isPresent()) {
            LOGGER.debug("User {} already logged in, redirecting to home.", currentUser.get().getUserEmail());
            return new ModelAndView("redirect:/home");
        }
        return new ModelAndView("landing");
    }
}
