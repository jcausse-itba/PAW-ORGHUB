package ar.edu.itba.paw.model.schemas;

public final class SubscriptionSchema {
    public static final String TABLE_NAME = "subs";
    public static final String USER_ID = UserSchema.ID;
    public static final String ORG_ID = OrganizationSchema.ID;

    // This class shall not be instantiated
    private SubscriptionSchema() {}
}
