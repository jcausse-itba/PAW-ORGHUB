package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.webapp.form.EmailMatchesDomainNameConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EmailMatchesDomainNameValidator implements ConstraintValidator<EmailMatchesDomainName, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (!(obj instanceof EmailMatchesDomainNameConstraint formConstraint)) {
            return false;
        }

        final String domainName = formConstraint.getDomainName();
        final String[] sep = formConstraint.getEmail().split("@");
        if (sep.length != 2) {
            return false;
        }

        if (!domainName.equals(sep[1])) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.email.domain.mismatch}")
                    .addPropertyNode(formConstraint.getEmailPropertyName()).addConstraintViolation();
            return false;
        }

        return true;
    }
}
