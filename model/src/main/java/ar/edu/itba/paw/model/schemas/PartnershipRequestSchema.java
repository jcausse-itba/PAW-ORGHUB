package ar.edu.itba.paw.model.schemas;

public class PartnershipRequestSchema {
    public static final String TABLE_NAME = "partnership_requests";
    public static final String ID_SEQUENCE_GENERATOR = "partnership_requests_requestid_seq";

    public static final String ID = "requestid";
    public static final String INSTITUTION_NAME = "institutionname";
    public static final String DOMAIN_NAME = "domainname";
    public static final String ADMIN_EMAIL = "adminemail";
    public static final String STATUS = "status";
    public static final String REQUEST_DATE = "requestdate";
    public static final String PROCESSED_DATE = "processeddate";

    // This class shall not be instantiated
    private PartnershipRequestSchema() {}
}
