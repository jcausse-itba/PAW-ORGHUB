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
public class ErrorController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorController.class);

    @GetMapping("/403")
    @PreAuthorize("permitAll()")
    public ModelAndView forbiddenError(
            @ModelAttribute("currentUser") Optional<User> currentUser
    ) {
        LOGGER.info("403 Forbidden. User: {}.", currentUser.map(User::getUserEmail).orElse("unknown"));

        ModelAndView mav = new ModelAndView("error");
        mav.addObject("errorCode", "403");
        mav.addObject("errorMessage", "No tienes permisos para realizar esta acción.");
        return mav;
    }
}
