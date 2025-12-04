package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.webapp.form.RegisterForm;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj instanceof RegisterForm form) {
            if (!form.getPassword().equals(form.getConfirmPassword())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("{validation.register.confirmPassword.match}")
                        .addPropertyNode("confirmPassword")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
