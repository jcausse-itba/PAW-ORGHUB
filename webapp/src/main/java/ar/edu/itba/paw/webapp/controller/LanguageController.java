package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
public class LanguageController {

    @Autowired
    private UserService userService;

    @RequestMapping("/changeLanguage")
    @PreAuthorize("permitAll()")
    public String changeLanguage(@RequestParam("lang") String lang, HttpServletRequest request) {
        
        Locale locale = Locale.forLanguageTag(lang);
        request.getSession().setAttribute("org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", locale);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            String userEmail = authentication.getName();
            User user = userService.findByEmail(userEmail);
            Language preferredLanguage = lang.equals("en") ? Language.ENGLISH : Language.SPANISH;
            userService.updatePreferredLanguage(user.getUserID(), preferredLanguage);
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/home";
    }
}
