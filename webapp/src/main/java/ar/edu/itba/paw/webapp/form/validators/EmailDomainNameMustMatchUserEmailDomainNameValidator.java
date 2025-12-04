package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.webapp.auth.WebPermissionService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailDomainNameMustMatchUserEmailDomainNameValidator implements ConstraintValidator<EmailDomainNameMustMatchUserEmailDomainName, String> {

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;   // Validated by @NotBlank
        }
        final String[] sep = email.trim().split("@");
        if (sep.length != 2) {
            return false;   // Not an Email
        }
        return sep[1].equals(WebPermissionService.getCurrentUserEmail().split("@")[1]);
    }
}
