package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.PostSchema;
import ar.edu.itba.paw.model.schemas.PostTagSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = PostSchema.TABLE_NAME)
public class Post {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = PostSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = PostSchema.ID_SEQUENCE_GENERATOR, sequenceName = PostSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = PostSchema.ID)
    private Long postID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = PostSchema.ORG_ID, nullable = false)
    private Organization organization;

    @Setter
    @Column(name = PostSchema.TITLE, length = Boundaries.CONTENT_TITLE_MAX_LEN, nullable = false)
    private String title;

    @Setter
    @Column(name = PostSchema.BODY, length = Boundaries.CONTENT_BODY_MAX_LEN, nullable = false)
    private String body;

    @Setter
    @Column(name = PostSchema.LIKES)
    private Long likeCount;

    @Setter
    @Column(name = PostSchema.COMMENTS)
    private Long commentCount;

    @Column(name = PostSchema.DATETIME)
    private LocalDateTime datetime;

    /*
     * A Set of Tag(s) that a Post may be tagged with. Given that a Post can be tagged with up to CONTENT_MAX_TAGS Tag(s),
     * this relation cannot grow indefinitely.
     */
    @ManyToMany(fetch = FetchType.LAZY) // OK
    @JoinTable(
            name = PostTagSchema.TABLE_NAME,
            joinColumns = @JoinColumn(name = PostTagSchema.POST_ID),
            inverseJoinColumns = @JoinColumn(name = PostTagSchema.TAG_ID)
    )
    @SuppressWarnings("FieldMayBeFinal")
    private Set<Tag> tags = new HashSet<>();

    public Post(final Organization organization, final String title, final String body, final LocalDateTime datetime, final Set<Tag> tags) {
        this.postID = null;
        this.organization = organization;
        this.title = title;
        this.body = body;
        this.likeCount = 0L;
        this.commentCount = 0L;
        this.datetime = datetime;
        if (tags != null) {
            this.tags = new HashSet<>(tags);
        }
    }

    public String getFormattedDateTime(final DateTimeFormatter formatter) {
        return datetime != null ? datetime.format(formatter) : null;
    }
}
