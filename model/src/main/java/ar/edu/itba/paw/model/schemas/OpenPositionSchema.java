package ar.edu.itba.paw.model.schemas;

public final class OpenPositionSchema {
    public static final String TABLE_NAME = "open_positions";
    public static final String ID_SEQUENCE_GENERATOR = "open_positions_positionid_seq";

    public static final String ID = "positionid";
    public static final String ORG_ID = "orgid";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String REQUIREMENTS = "requirements";
    public static final String WORKHOURS = "workhours";
    public static final String DATETIME = "datetime";
    public static final String ORGANIGRAM_POSITION_ID = "organigrampositionid";
    public static final String STATUS = "status";

    // This class shall not be instantiated
    private OpenPositionSchema() {}
}
