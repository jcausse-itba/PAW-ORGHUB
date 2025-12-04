package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.Tag;
import lombok.*;

import java.io.Serializable;

@Deprecated
@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class OpenPositionTagCompositeID implements Serializable {
    private OpenPosition position;
    private Tag tag;
}
