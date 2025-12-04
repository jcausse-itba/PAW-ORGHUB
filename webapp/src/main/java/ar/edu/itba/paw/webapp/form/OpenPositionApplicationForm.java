package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validators.ValidApplicationForm;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@ValidApplicationForm
public class OpenPositionApplicationForm {
    private Map<Long, String> answers = new HashMap<>();

    public OpenPositionApplicationForm() {
        answers = new HashMap<>();
    }

    public void addAnswer(Long questionID, String answer) {
        this.answers.put(questionID, answer);
    }

    public String getAnswer(Long questionID) {
        return answers.getOrDefault(questionID, "");
    }

    public void setAnswer(Long questionID, String answer) {
        this.answers.put(questionID, answer);
    }
}
