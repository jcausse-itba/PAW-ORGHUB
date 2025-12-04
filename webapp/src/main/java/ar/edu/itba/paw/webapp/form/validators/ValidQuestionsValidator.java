package ar.edu.itba.paw.webapp.form.validators;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

@Component
public class ValidQuestionsValidator implements ConstraintValidator<ValidQuestions, List<String>> {

    private static final int MAX_QUESTION_LENGTH = 500;

    @Override
    public void initialize(ValidQuestions constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> questions, ConstraintValidatorContext context) {
        if (questions == null || questions.isEmpty()) {
            return true;
        }

        String firstQuestion = questions.get(0);
        boolean isFirstEmpty = firstQuestion == null || firstQuestion.trim().isEmpty();
        
        if (isFirstEmpty) {
            for (int i = 0; i < questions.size(); i++) {
                String question = questions.get(i);
                if (question != null && !question.trim().isEmpty()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        "{validation.position.questions.allEmpty}"
                    ).addConstraintViolation();
                    return false;
                }
            }
        } else {
            for (int i = 0; i < questions.size(); i++) {
                String question = questions.get(i);
                
                if (question == null || question.trim().isEmpty()) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        "{validation.position.questions.empty}"
                    ).addConstraintViolation();
                    return false;
                }
                
                if (question.length() > MAX_QUESTION_LENGTH) {
                    context.disableDefaultConstraintViolation();
                    context.buildConstraintViolationWithTemplate(
                        "{validation.position.questions.max}"
                    ).addConstraintViolation();
                    return false;
                }
            }
        }

        return true;
    }
}
