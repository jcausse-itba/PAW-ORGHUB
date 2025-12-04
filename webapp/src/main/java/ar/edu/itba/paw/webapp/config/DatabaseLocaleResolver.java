package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.interfaces.service.UserService;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class DatabaseLocaleResolver implements LocaleResolver {

    private final UserService userService;

    public DatabaseLocaleResolver(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        Locale sessionLocale = (Locale) request.getSession().getAttribute("org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE");
        if (sessionLocale != null) {
            request.setAttribute("org.springframework.web.servlet.i18n.LocaleResolver.LOCALE", sessionLocale);
            return sessionLocale;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
                String userEmail = authentication.getName();
                User user = userService.findByEmail(userEmail);
                Language preferredLanguage = user.getPreferredLanguage();
                Locale dbLocale;
                if (preferredLanguage == Language.ENGLISH) {
                    dbLocale = Locale.forLanguageTag("en");
                } else {
                    dbLocale = Locale.forLanguageTag("es");
                }
                request.setAttribute("org.springframework.web.servlet.i18n.LocaleResolver.LOCALE", dbLocale);
                return dbLocale;
        }

        Locale defaultLocale = Locale.forLanguageTag("es");
        request.setAttribute("org.springframework.web.servlet.i18n.LocaleResolver.LOCALE", defaultLocale);
        return defaultLocale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        request.getSession().setAttribute("org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE", locale);
    }
}
