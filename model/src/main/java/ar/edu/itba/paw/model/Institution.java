package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.InstitutionSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = InstitutionSchema.TABLE_NAME)
public class Institution {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = InstitutionSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = InstitutionSchema.ID_SEQUENCE_GENERATOR, sequenceName = InstitutionSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = InstitutionSchema.ID)
    private Long institutionID;

    @Column(name = InstitutionSchema.NAME, length = Boundaries.INST_NAME_MAX_LEN, nullable = false)
    private String institutionName;

    @EqualsAndHashCode.Include
    @Column(name = InstitutionSchema.DOMAIN_NAME, length = Boundaries.INST_DOMAIN_NAME_MAX_LEN, nullable = false, unique = true)
    private String domainName;

    @Column(name = InstitutionSchema.ADMIN_EMAIL, length = Boundaries.EMAIL_MAX_LEN, nullable = false, unique = true)
    // TODO: Map to UserEntity
    private String adminEmail;

    public Institution(final Long ID, final String name, final String domainName, final String adminEmail) {
        this.institutionID = ID;
        this.institutionName = name;
        this.domainName = domainName;
        this.adminEmail = adminEmail;
    }

    public Institution(final String name, final String domainName, final String adminEmail) {
        this.institutionID = null;
        this.institutionName = name;
        this.domainName = domainName;
        this.adminEmail = adminEmail;
    }
}
