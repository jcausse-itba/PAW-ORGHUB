package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.CommentSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = CommentSchema.TABLE_NAME)
public class Comment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = CommentSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = CommentSchema.ID_SEQUENCE_GENERATOR, sequenceName = CommentSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = CommentSchema.ID)
    private Long commentID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = CommentSchema.USER_ID, nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = CommentSchema.POST_ID, nullable = false)
    private Post post;

    @Column(name = CommentSchema.CONTENT, length = Boundaries.CONTENT_COMMENT_MAX_LEN, nullable = false)
    private String content;

    public Comment(final User user, final Post post, final String content) {
        this.commentID = null;
        this.user = user;
        this.post = post;
        this.content = content;
    }

    // Convenience getter for JSP compatibility
    public Long getUserID() {
        return user != null ? user.getUserID() : null;
    }
}
