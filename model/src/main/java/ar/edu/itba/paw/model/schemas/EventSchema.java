package ar.edu.itba.paw.model.schemas;

public final class EventSchema {
    public static final String TABLE_NAME = "events";
    public static final String ID_SEQUENCE_GENERATOR = "events_eventid_seq";

    public static final String ID = "eventid";
    public static final String ORG_ID = "orgid";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String LOCATION = "location";
    public static final String EVENT_DATE = "eventdate";
    public static final String DATETIME = "datetime";
    public static final String IMAGE_ID = "imageid";

    // This class shall not be instantiated
    private EventSchema() {}
}
