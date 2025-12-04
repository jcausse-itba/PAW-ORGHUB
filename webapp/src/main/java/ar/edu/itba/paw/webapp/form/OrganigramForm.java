package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.UserWithEmailMustExist;
import ar.edu.itba.paw.webapp.form.validators.UniqueSortOrderPerParent;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@UniqueSortOrderPerParent
public class OrganigramForm {

    @NotBlank(message = "{validation.organigram.positionName.required}")
    @Size(min = 3, max = 150, message = "{validation.organigram.positionName.length}")
    private String positionName;

    @NotBlank(message = "{validation.organigram.description.required}")
    @Size(max = 500, message = "{validation.organigram.description.max}")
    private String description;

    private Long parentPositionID;

    @NotNull(message = "{validation.organigram.sortOrder.required}")
    private Integer sortOrder;

    @Email(message = "{validation.org.email.invalid}")
    @UserWithEmailMustExist
    private String assignedUserEmail;

    private Long organizationID;

    public OrganigramForm() {
        positionName = "";
        description = "";
        parentPositionID = null;
        sortOrder = 0;
        assignedUserEmail = "";
        organizationID = null;
    }
}
