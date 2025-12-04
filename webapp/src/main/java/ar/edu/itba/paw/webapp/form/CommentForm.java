package ar.edu.itba.paw.webapp.form;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Setter
public class CommentForm {
    private long postID;

    @NotBlank(message = "{validation.comment.empty}")
    @Size(min=3, max = 500, message = "{validation.comment.length}")
    private String content;
}