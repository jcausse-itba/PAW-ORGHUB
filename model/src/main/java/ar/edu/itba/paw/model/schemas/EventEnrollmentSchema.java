package ar.edu.itba.paw.model.schemas;

public class EventEnrollmentSchema {
    public static final String TABLE_NAME = "events_enrollments";
    public static final String ID_SEQUENCE_GENERATOR = "events_enrollments_enrollmentid_seq";

    public static final String ID = "enrollmentid";
    public static final String EVENT_ID = EventSchema.ID;
    public static final String USER_ID = UserSchema.ID;
    public static final String STATUS = "status";

    // This class shall not be instantiated
    private EventEnrollmentSchema() {}
}
