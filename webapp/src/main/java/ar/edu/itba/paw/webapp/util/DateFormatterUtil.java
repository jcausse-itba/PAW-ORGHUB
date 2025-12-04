package ar.edu.itba.paw.webapp.util;

import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormatterUtil {
    
    public static DateTimeFormatter createDateFormatter(HttpServletRequest request) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        Locale locale;
        
        if (localeResolver != null) {
            locale = localeResolver.resolveLocale(request);
        } else {
            locale = Locale.forLanguageTag("es");
        }
        
        if (locale.getLanguage().equals("en")) {
            return DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        } else {
            return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        }
    }
}
