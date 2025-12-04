package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidApplicationFormValidator.class)
@Documented
public @interface ValidApplicationForm {
    String message() default "{validation.application.answerAll}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
