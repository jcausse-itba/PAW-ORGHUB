package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.schemas.OpenPositionApplicationSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OpenPositionApplicationSchema.TABLE_NAME)
public class OpenPositionApplication {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OpenPositionApplicationSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OpenPositionApplicationSchema.ID_SEQUENCE_GENERATOR, sequenceName = OpenPositionApplicationSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OpenPositionApplicationSchema.ID)
    private Long applicationID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionApplicationSchema.POSITION_ID, nullable = false)
    private OpenPosition openPosition;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionApplicationSchema.USER_ID, nullable = false)
    private User user;

    @Setter
    @Enumerated(EnumType.ORDINAL)
    @Column(name = OpenPositionApplicationSchema.STATUS, nullable = false)
    private Status status;

    public OpenPositionApplication(final OpenPosition openPosition, final User user, final Status status) {
        this.applicationID = null;
        this.openPosition = openPosition;
        this.user = user;
        this.status = status;
    }
}
