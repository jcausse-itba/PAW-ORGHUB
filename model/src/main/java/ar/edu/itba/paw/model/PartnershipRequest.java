package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.schemas.PartnershipRequestSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = PartnershipRequestSchema.TABLE_NAME)
public class PartnershipRequest {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = PartnershipRequestSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = PartnershipRequestSchema.ID_SEQUENCE_GENERATOR, sequenceName = PartnershipRequestSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = PartnershipRequestSchema.ID)
    private Long requestID;

    @Column(name = PartnershipRequestSchema.INSTITUTION_NAME, length = Boundaries.INST_NAME_MAX_LEN, nullable = false)
    private String institutionName;

    @EqualsAndHashCode.Include
    @Column(name = PartnershipRequestSchema.DOMAIN_NAME, length = Boundaries.INST_DOMAIN_NAME_MAX_LEN, nullable = false, unique = true)
    private String domainName;

    @Column(name = PartnershipRequestSchema.ADMIN_EMAIL, length = Boundaries.EMAIL_MAX_LEN, nullable = false, unique = true)
    private String adminEmail;

    @Setter
    @Column(name = PartnershipRequestSchema.STATUS, nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column(name = PartnershipRequestSchema.REQUEST_DATE, nullable = false)
    private LocalDateTime requestDate;

    @Setter
    @Column(name = PartnershipRequestSchema.PROCESSED_DATE)
    private LocalDateTime processedDate;

    public PartnershipRequest(final String institutionName, final String domainName, final String adminEmail,
                              final Status status, final LocalDateTime requestDate
    ) {
        this.requestID = null;
        this.institutionName = institutionName;
        this.domainName = domainName;
        this.adminEmail = adminEmail;
        this.status = status;
        this.requestDate = requestDate;
        this.processedDate = null;
    }

    public String getFormattedRequestDate(final DateTimeFormatter formatter) {
        return requestDate != null ? requestDate.format(formatter) : null;
    }
}
