package ar.edu.itba.paw.model.schemas;

public final class OrganizationSchema {
    public static final String TABLE_NAME = "orgs";
    public static final String ID_SEQUENCE_GENERATOR = "orgs_orgid_seq";

    public static final String ID = "orgid";
    public static final String EMAIL = "orgemail";
    public static final String NAME = "orgname";
    public static final String SOCIAL_1 = "sociallink1";
    public static final String SOCIAL_2 = "sociallink2";
    public static final String DESCRIPTION = "description";
    public static final String INSTITUTION_ID = InstitutionSchema.ID;

    // This class shall not be instantiated
    private OrganizationSchema() {}
}
