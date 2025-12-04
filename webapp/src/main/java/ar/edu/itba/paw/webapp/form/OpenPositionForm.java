package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.ValidQuestions;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OpenPositionForm {

    @NotBlank(message = "{validation.position.title.required}")
    @Size(min = 5, max = 150, message = "{validation.position.title.length}")
    private String title;

    @NotBlank(message = "{validation.position.description.required}")
    @Size(max = 5000, message = "{validation.position.description.max}")
    private String description;

    @NotBlank(message = "{validation.position.requirements.required}")
    @Size(max = 5000, message = "{validation.position.requirements.max}")
    private String requirements;

    @Size(max = 150, message = "{validation.position.workhours.max}")
    private String workhours;

    @Size(max = 10, message = "{validation.position.questions.maxCount}")
    @ValidQuestions
    private List <String> questions;

    @Size(max = 3, message = "{validation.tags.max}")
    private List<Long> tags;

    private Long organigramPositionID;

    public OpenPositionForm() {
        title = "";
        description = "";
        requirements = "";
        workhours = "";
        questions = new ArrayList<>();
        tags = new ArrayList<>();
        organigramPositionID = null;
    }
}
