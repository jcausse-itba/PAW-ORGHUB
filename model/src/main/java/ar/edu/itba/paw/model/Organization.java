package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.OrganizationSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OrganizationSchema.TABLE_NAME)
public class Organization {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OrganizationSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OrganizationSchema.ID_SEQUENCE_GENERATOR, sequenceName = OrganizationSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OrganizationSchema.ID)
    private Long orgID;

    @EqualsAndHashCode.Include
    @Column(name = OrganizationSchema.EMAIL, length = Boundaries.EMAIL_MAX_LEN, nullable = false, unique = true)
    private String orgEmail;

    @Column(name = OrganizationSchema.NAME, length = Boundaries.ORG_NAME_MAX_LEN, nullable = false)
    private String orgName;

    @Column(name = OrganizationSchema.SOCIAL_1, length = Boundaries.URL_MAX_LEN)
    private String socialLink1;

    @Column(name = OrganizationSchema.SOCIAL_2, length = Boundaries.URL_MAX_LEN)
    private String socialLink2;

    @Column(name = OrganizationSchema.DESCRIPTION, length = Boundaries.ORG_DESCRIPTION_MAX_LEN)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OrganizationSchema.INSTITUTION_ID, nullable = false)
    private Institution institution;

    public Organization(final String orgEmail, final String orgName,
                        final String socialLink1, final String socialLink2, final String description,
                        final Institution institution
    ) {
        this.orgID = null;
        this.orgEmail = orgEmail;
        this.orgName = orgName;
        this.socialLink1 = socialLink1;
        this.socialLink2 = socialLink2;
        this.description = description;
        this.institution = institution;
    }
}
