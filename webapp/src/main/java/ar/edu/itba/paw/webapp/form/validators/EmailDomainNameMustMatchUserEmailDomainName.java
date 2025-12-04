package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailDomainNameMustMatchUserEmailDomainNameValidator.class)
@Documented
public @interface EmailDomainNameMustMatchUserEmailDomainName {
    String message() default "{validation.email.domain.mustMatch.institution}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
