package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class LikeCompositeID implements Serializable {
    private User user;
    private Post post;
}
