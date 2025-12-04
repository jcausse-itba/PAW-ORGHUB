package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserWithEmailMustExistValidator.class)
@Documented
public @interface UserWithEmailMustExist {
    String message() default "{validation.user.email.notExists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
