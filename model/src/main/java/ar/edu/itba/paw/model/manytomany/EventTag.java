package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.Tag;

import ar.edu.itba.paw.model.schemas.EventTagSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Deprecated
@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = EventTagSchema.TABLE_NAME)
@IdClass(EventTagCompositeID.class)
public class EventTag {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = EventTagSchema.EVENT_ID, nullable = false)
    private Event event;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = EventTagSchema.TAG_ID, nullable = false)
    private Tag tag;

    public EventTag(final Event event, final Tag tag) {
        this.event = event;
        this.tag = tag;
    }
}
