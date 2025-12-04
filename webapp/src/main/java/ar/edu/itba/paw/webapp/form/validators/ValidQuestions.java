package ar.edu.itba.paw.webapp.form.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidQuestionsValidator.class)
public @interface ValidQuestions {
    String message() default "{validation.position.questions.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
