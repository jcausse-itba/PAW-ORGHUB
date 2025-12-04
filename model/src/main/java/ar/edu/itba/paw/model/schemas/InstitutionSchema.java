package ar.edu.itba.paw.model.schemas;

public final class InstitutionSchema {
    public static final String TABLE_NAME = "institutions";
    public static final String ID_SEQUENCE_GENERATOR = "institutions_instid_seq";

    public static final String ID = "instid";
    public static final String NAME = "instname";
    public static final String DOMAIN_NAME = "domainname";
    public static final String ADMIN_EMAIL = "adminemail";

    // This class shall not be instantiated
    private InstitutionSchema() {}
}
