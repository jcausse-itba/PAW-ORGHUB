package ar.edu.itba.paw.model.schemas;

public final class OpenPositionTagSchema {
    public static final String TABLE_NAME = "open_positions_tags";
    public static final String POSITION_ID = OpenPositionSchema.ID;
    public static final String TAG_ID = TagSchema.ID;

    // This class shall not be instantiated
    private OpenPositionTagSchema() {}
}
