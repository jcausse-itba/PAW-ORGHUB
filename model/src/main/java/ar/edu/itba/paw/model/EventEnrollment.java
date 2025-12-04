package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.schemas.EventEnrollmentSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = EventEnrollmentSchema.TABLE_NAME)
public class EventEnrollment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = EventEnrollmentSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = EventEnrollmentSchema.ID_SEQUENCE_GENERATOR, sequenceName = EventEnrollmentSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = EventEnrollmentSchema.ID)
    private Long enrollmentID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = EventEnrollmentSchema.EVENT_ID, nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = EventEnrollmentSchema.USER_ID, nullable = false)
    private User user;

    @Setter
    @Enumerated(EnumType.ORDINAL)
    @Column(name = EventEnrollmentSchema.STATUS, nullable = false)
    private Status status;

    public EventEnrollment(final Event event, final User user, final Status status) {
        this.enrollmentID = null;
        this.event = event;
        this.user = user;
        this.status = status;
    }
}
