package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.schemas.OpenPositionTagSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Deprecated
@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OpenPositionTagSchema.TABLE_NAME)
@IdClass(OpenPositionTagCompositeID.class)
public class OpenPositionTag {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = OpenPositionTagSchema.POSITION_ID, nullable = false)
    private OpenPosition position;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = OpenPositionTagSchema.TAG_ID, nullable = false)
    private Tag tag;

    public OpenPositionTag(final OpenPosition position, final Tag tag ) {
        this.position = position;
        this.tag = tag;
    }
}
