package ar.edu.itba.paw.model.schemas;

public final class OrganigramSchema {
    public static final String TABLE_NAME = "organigrams";
    public static final String ID_SEQUENCE_GENERATOR = "organigrams_organigramid_seq";

    public static final String ID = "organigramid";
    public static final String ORG_ID = "orgid";
    public static final String POSITION_NAME = "positionname";
    public static final String DESCRIPTION = "description";
    public static final String PARENT_POSITION_ID = "parentpositionid";
    public static final String LEVEL = "level";
    public static final String SORT_ORDER = "sortorder";
    public static final String ASSIGNED_USER_ID = "assigneduserid";

    // This class shall not be instantiated
    private OrganigramSchema() {}
}
