package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.TagSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = TagSchema.TABLE_NAME)
public class Tag {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TagSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = TagSchema.ID_SEQUENCE_GENERATOR, sequenceName = TagSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = TagSchema.ID)
    private Long tagID;

    @EqualsAndHashCode.Include
    @Column(name = TagSchema.NAME, length = Boundaries.TAG_MAX_LEN, nullable = false, unique = true)
    private String tagName;

    public Tag(final String tagName) {
        this.tagID = null;
        this.tagName = tagName;
    }
}
