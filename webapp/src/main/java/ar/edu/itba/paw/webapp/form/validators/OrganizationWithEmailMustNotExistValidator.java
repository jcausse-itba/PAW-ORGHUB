package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.interfaces.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OrganizationWithEmailMustNotExistValidator implements ConstraintValidator<OrganizationWithEmailMustNotExist, String> {

    @Autowired
    private OrganizationService organizationService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return false;    // Validated by @NotBlank
        }
        return !organizationService.existsEmail(email);
    }
}
