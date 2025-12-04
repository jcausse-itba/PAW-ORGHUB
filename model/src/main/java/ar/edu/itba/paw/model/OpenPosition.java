package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.schemas.OpenPositionSchema;
import ar.edu.itba.paw.model.schemas.OpenPositionTagSchema;
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
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OpenPositionSchema.TABLE_NAME)
public class OpenPosition {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OpenPositionSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OpenPositionSchema.ID_SEQUENCE_GENERATOR, sequenceName = OpenPositionSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OpenPositionSchema.ID)
    private Long positionID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionSchema.ORG_ID, nullable = false)
    private Organization organization;

    @Column(name = OpenPositionSchema.TITLE, length = Boundaries.CONTENT_TITLE_MAX_LEN, nullable = false)
    private String title;

    @Column(name = OpenPositionSchema.DESCRIPTION, length = Boundaries.OPEN_POSITION_DESCRIPTION_MAX_LEN, nullable = false)
    private String description;

    @Column(name = OpenPositionSchema.REQUIREMENTS, length = Boundaries.OPEN_POSITION_REQUIREMENTS_MAX_LEN, nullable = false)
    private String requirements;

    @Column(name = OpenPositionSchema.WORKHOURS, length = Boundaries.OPEN_POSITION_WORKHOURS_MAX_LEN)
    private String workHours;

    @Column(name = OpenPositionSchema.DATETIME)
    private LocalDateTime datetime;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionSchema.ORGANIGRAM_POSITION_ID)
    private Organigram organigramPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = OpenPositionSchema.STATUS, length = Boundaries.ENUM_CONSTANT_MAX_LEN, nullable = false)
    private OpenPositionStatus status;

    /*
     * A Set of Tag(s) that an OpenPosition may be tagged with. Given that an OpenPosition can be tagged with up to CONTENT_MAX_TAGS
     * Tag(s), this relation cannot grow indefinitely.
     */
    @ManyToMany(fetch = FetchType.LAZY) // OK
    @JoinTable(
            name = OpenPositionTagSchema.TABLE_NAME,
            joinColumns = @JoinColumn(name = OpenPositionTagSchema.POSITION_ID),
            inverseJoinColumns = @JoinColumn(name = OpenPositionTagSchema.TAG_ID)
    )
    @SuppressWarnings("FieldMayBeFinal")
    private Set<Tag> tags = new HashSet<>();

    public OpenPosition(final Organization organization, final String title, final String description, final String requirements, final String workHours, final LocalDateTime datetime, final Organigram organigramPosition, final Set<Tag> tags) {
        this.positionID = null;
        this.organization = organization;
        this.title = title;
        this.description = description;
        this.requirements = requirements;
        this.workHours = workHours;
        this.datetime = datetime;
        this.organigramPosition = organigramPosition;
        this.status = OpenPositionStatus.ACTIVE;
        if (tags != null) {
            this.tags = new HashSet<>(tags);
        }
    }

    public String getFormattedDateTime(final DateTimeFormatter formatter) {
        return datetime != null ? datetime.format(formatter) : null;
    }
}
