package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.Tag;
import lombok.*;

import java.io.Serializable;

@Deprecated
@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class PostTagCompositeID implements Serializable {
    private Post post;
    private Tag tag;
}
