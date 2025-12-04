package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.webapp.form.OpenPositionApplicationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

public class ValidApplicationFormValidator implements ConstraintValidator<ValidApplicationForm, OpenPositionApplicationForm> {

    @Override
    public void initialize(ValidApplicationForm constraintAnnotation) {
    }

    @Override
    public boolean isValid(OpenPositionApplicationForm form, ConstraintValidatorContext context) {
        if (form == null || form.getAnswers() == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.application.answerAll}")
                    .addPropertyNode("answers")
                    .addConstraintViolation();
            return false;
        }
        
        if (form.getAnswers().isEmpty()) {
            return true;
        }

        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        for (Map.Entry<Long, String> entry : form.getAnswers().entrySet()) {
            String answer = entry.getValue();
            String fieldName = "answers[" + entry.getKey() + "]";
            
            if (answer == null || answer.trim().isEmpty()) {
                context.buildConstraintViolationWithTemplate("{validation.application.answerRequired}")
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                isValid = false;
            } else if (answer.trim().length() < 10) {
                context.buildConstraintViolationWithTemplate("{validation.application.answerMin}")
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                isValid = false;
            } else if (answer.length() > 5000) {
                context.buildConstraintViolationWithTemplate("{validation.application.answerMax}")
                        .addPropertyNode(fieldName)
                        .addConstraintViolation();
                isValid = false;
            }
        }

        return isValid;
    }
}
