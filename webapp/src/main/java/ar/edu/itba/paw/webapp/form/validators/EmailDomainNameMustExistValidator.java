package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.interfaces.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailDomainNameMustExistValidator implements ConstraintValidator<EmailDomainNameMustExist, String> {

    @Autowired
    private InstitutionService institutionService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;
        }
        final String[] split = email.strip().split("@");
        if (split.length != 2) {
            return false;
        }
        return institutionService.existsDomainName(split[1]);
    }
}
