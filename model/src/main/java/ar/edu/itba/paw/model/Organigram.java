package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.OrganigramSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OrganigramSchema.TABLE_NAME)
public class Organigram {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OrganigramSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OrganigramSchema.ID_SEQUENCE_GENERATOR, sequenceName = OrganigramSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OrganigramSchema.ID)
    private Long organigramID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OrganigramSchema.ORG_ID, nullable = false)
    private Organization organization;

    @Column(name = OrganigramSchema.POSITION_NAME, length = Boundaries.ORGANIGRAM_POSITION_NAME_MAX_LEN, nullable = false)
    private String positionName;

    @Column(name = OrganigramSchema.DESCRIPTION, length = Boundaries.ORGANIGRAM_DESCRIPTION_MAX_LEN, nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OrganigramSchema.PARENT_POSITION_ID)
    private Organigram parentPosition;

    @Column(name = OrganigramSchema.LEVEL, nullable = false)
    private Integer level;

    @Column(name = OrganigramSchema.SORT_ORDER, nullable = false)
    private Integer sortOrder;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OrganigramSchema.ASSIGNED_USER_ID)
    private User assignedUser;

    // orphanRemoval = true: When a parent position is deleted, all child positions are also deleted
    // This maintains referential integrity in the organizational hierarchy
    // Organigrams are not meant to grow indefinitely
    @OneToMany(mappedBy = "parentPosition", fetch = FetchType.LAZY, orphanRemoval = true) // OK
    private List<Organigram> childPositions;

    public Organigram(final Organization organization, final String positionName, final String description, final Organigram parentPosition, final Integer level, final Integer sortOrder, final User assignedUser ) {
        this.organigramID = null;
        this.organization = organization;
        this.positionName = positionName;
        this.description = description;
        this.parentPosition = parentPosition;
        this.level = level;
        this.sortOrder = sortOrder;
        this.assignedUser = assignedUser;
    }
}
