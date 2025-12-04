package ar.edu.itba.paw.model.schemas;

public final class OpenPositionApplicationSchema {
    public static final String TABLE_NAME = "open_position_applications";
    public static final String ID_SEQUENCE_GENERATOR = "open_position_applications_applicationid_seq";

    public static final String ID = "applicationid";
    public static final String POSITION_ID = "positionid";
    public static final String USER_ID = "userid";
    public static final String STATUS = "status";

    // This class shall not be instantiated
    private OpenPositionApplicationSchema() {}
}
