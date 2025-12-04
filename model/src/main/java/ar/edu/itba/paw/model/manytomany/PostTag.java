package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.schemas.PostTagSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Deprecated
@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = PostTagSchema.TABLE_NAME)
@IdClass(PostTagCompositeID.class)
public class PostTag {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PostTagSchema.POST_ID, nullable = false)
    private Post post;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = PostTagSchema.TAG_ID, nullable = false)
    private Tag tag;

    public PostTag(final Post post, final Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}
