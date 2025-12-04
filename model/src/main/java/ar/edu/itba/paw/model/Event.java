package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.image.Image;
import ar.edu.itba.paw.model.schemas.EventSchema;
import ar.edu.itba.paw.model.schemas.EventTagSchema;
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
@Table(name = EventSchema.TABLE_NAME)
public class Event {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EventSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = EventSchema.ID_SEQUENCE_GENERATOR, sequenceName = EventSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = EventSchema.ID)
    private Long eventID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = EventSchema.ORG_ID, nullable = false)
    private Organization organization;

    @Setter
    @Column(name = EventSchema.TITLE, length = Boundaries.CONTENT_TITLE_MAX_LEN, nullable = false)
    private String title;

    @Setter
    @Column(name = EventSchema.DESCRIPTION, length = Boundaries.CONTENT_BODY_MAX_LEN, nullable = false)
    private String description;

    @Setter
    @Column(name = EventSchema.LOCATION, length = Boundaries.CONTENT_TITLE_MAX_LEN, nullable = false)
    private String location;

    @Setter
    @Column(name = EventSchema.EVENT_DATE, nullable = false)
    private LocalDateTime eventDate;

    @Column(name = EventSchema.DATETIME)
    private LocalDateTime datetime;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = EventSchema.IMAGE_ID)
    private Image image;

    /*
     * A Set of Tag(s) that an Event may be tagged with. Given that an Event can be tagged with up to CONTENT_MAX_TAGS Tag(s),
     * this relation cannot grow indefinitely.
     */
    @ManyToMany(fetch = FetchType.LAZY) // OK
    @JoinTable(
            name = EventTagSchema.TABLE_NAME,
            joinColumns = @JoinColumn(name = EventTagSchema.EVENT_ID),
            inverseJoinColumns = @JoinColumn(name = EventTagSchema.TAG_ID)
    )
    @SuppressWarnings("FieldMayBeFinal")
    private Set<Tag> tags = new HashSet<>();

    public Event(
        final Organization organization,
        final String title,
        final String description,
        final String location,
        final LocalDateTime eventDate,
        final LocalDateTime datetime,
        final Set<Tag> tags
    ) {
        this.eventID = null;
        this.organization = organization;
        this.title = title;
        this.description = description;
        this.location = location;
        this.eventDate = eventDate;
        this.datetime = datetime;
        if (tags != null) {
            this.tags = new HashSet<>(tags);
        }
    }

    public String getFormattedDateTime(final DateTimeFormatter formatter) {
        return datetime != null ? datetime.format(formatter) : null;
    }

    public String getFormattedEventDateTime(final DateTimeFormatter formatter) {
        return eventDate != null ? eventDate.format(formatter) : null;
    }

    public boolean isFinished() {
        return eventDate != null && eventDate.isBefore(LocalDateTime.now());
    }
}
