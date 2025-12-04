package ar.edu.itba.paw.model.schemas;

public final class TagSchema {
    public static final String TABLE_NAME = "tags";
    public static final String ID_SEQUENCE_GENERATOR = "tags_tagid_seq";

    public static final String ID = "tagid";
    public static final String NAME = "tagname";

    // This class shall not be instantiated
    private TagSchema() {}
}
