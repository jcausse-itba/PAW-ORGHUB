package ar.edu.itba.paw.model.schemas;

public final class EventTagSchema {
    public static final String TABLE_NAME = "events_tags";
    public static final String EVENT_ID = EventSchema.ID;
    public static final String TAG_ID = TagSchema.ID;

    // This class shall not be instantiated
    private EventTagSchema() {}
}
