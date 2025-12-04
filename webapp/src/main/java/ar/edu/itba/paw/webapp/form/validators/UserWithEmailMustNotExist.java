package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserWithEmailMustNotExistValidator.class)
@Documented
public @interface UserWithEmailMustNotExist {
    String message() default "{validation.user.email.alreadyExists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
