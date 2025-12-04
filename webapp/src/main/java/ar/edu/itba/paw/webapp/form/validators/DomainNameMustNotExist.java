package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DomainNameMustNotExistValidator.class)
@Documented
public @interface DomainNameMustNotExist {
    String message() default "{validation.domain.alreadyExists}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
