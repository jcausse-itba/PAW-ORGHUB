package ar.edu.itba.paw.model.schemas;

public final class UserSchema {
    public static final String TABLE_NAME = "users";
    public static final String ID_SEQUENCE_GENERATOR = "users_userid_seq";

    public static final String ID = "userid";
    public static final String EMAIL = "useremail";
    public static final String FIRST_NAME = "firstname";
    public static final String LAST_NAME = "lastname";
    public static final String ENC_PASS = "encodedpassword";
    public static final String PREF_LANG = "preferredlanguage";
    public static final String INSTITUTION_ID = "instid";

    // This class shall not be instantiated
    private UserSchema() {}
}
