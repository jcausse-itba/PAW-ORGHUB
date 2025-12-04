package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDomainNameMustExistValidator.class)
@Documented
public @interface EmailDomainNameMustExist {
    String message() default "{validation.email.domain.mustExist}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
