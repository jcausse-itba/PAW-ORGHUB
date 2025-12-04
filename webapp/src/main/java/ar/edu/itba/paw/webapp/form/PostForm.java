package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter @Setter
public class PostForm {
    private long orgID;

    @NotBlank(message = "{validation.post.title.required}")
    @Size(min = 3, max = 150, message = "{validation.post.title.length}")
    private String title;

    @NotBlank(message = "{validation.post.body.required}")
    @Size(min = 10, max = 5000, message = "{validation.post.body.length}")
    private String body;

    @Size(max = 3, message = "{validation.tags.max}")
    private List<Long> tags;

    public PostForm() {
        orgID = 0L;
        title = "";
        body = "";
        tags= List.of();
    }
}
