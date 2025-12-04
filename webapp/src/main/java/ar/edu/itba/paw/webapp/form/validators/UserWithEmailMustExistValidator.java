package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import ar.edu.itba.paw.interfaces.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

public class UserWithEmailMustExistValidator implements ConstraintValidator <UserWithEmailMustExist, String> {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;    // Catched by @NotBlank and @NotNull
        }
        return userService.existsEmail(email);
    }
}
