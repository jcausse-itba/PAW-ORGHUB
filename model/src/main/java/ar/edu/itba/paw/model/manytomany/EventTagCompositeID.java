package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.Tag;
import lombok.*;

import java.io.Serializable;

@Deprecated
@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class EventTagCompositeID implements Serializable {
    private Event event;
    private Tag tag;
}
