package ar.edu.itba.paw.model.schemas;

public final class UserRoleSchema {
    public static final String TABLE_NAME = "users_roles";
    public static final String USER_ID = UserSchema.ID;
    public static final String ORG_ID = OrganizationSchema.ID;
    public static final String ROLE_ID = "roleid";

    // This class shall not be instantiated
    private UserRoleSchema() {}
}
