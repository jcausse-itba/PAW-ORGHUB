package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueSortOrderPerParentValidator.class)
@Documented
public @interface UniqueSortOrderPerParent {
    String message() default "{validation.organigram.sortOrder.duplicate.parent}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
