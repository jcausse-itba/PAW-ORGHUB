package ar.edu.itba.paw.webapp.form.validators;

import ar.edu.itba.paw.interfaces.service.OrganigramService;
import ar.edu.itba.paw.webapp.form.OrganigramForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

public class UniqueSortOrderPerParentValidator implements ConstraintValidator<UniqueSortOrderPerParent, OrganigramForm> {

    @Autowired
    private OrganigramService organigramService;

    @Override
    public void initialize(UniqueSortOrderPerParent constraintAnnotation) {
    }

    @Override
    public boolean isValid(OrganigramForm form, ConstraintValidatorContext context) {
        if (form == null || form.getSortOrder() == null) {
            return true;
        }
        if (form.getOrganizationID() == null || form.getOrganizationID() <= 0) {
            return true;
        }
        
        boolean exists = organigramService.existsSortOrderInParent(form.getParentPositionID(), form.getSortOrder(), form.getOrganizationID());
        
        if (exists) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.organigram.sortOrder.duplicate.parent}")
                    .addPropertyNode("sortOrder")
                    .addConstraintViolation();
        }
        
        return !exists;
    }
}
