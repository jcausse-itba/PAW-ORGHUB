package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.interfaces.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;

public class DomainNameMustNotExistValidator implements ConstraintValidator <DomainNameMustNotExist, String> {

    @Autowired
    private InstitutionService institutionService;

    @Override
    public boolean isValid(String domainName, ConstraintValidatorContext context) {
        if (domainName == null || domainName.isBlank()) {
            return true;    // Catched by @NotBlank and @NotNull
        }
        return !institutionService.existsDomainName(domainName);
    }
}
