package ar.edu.itba.paw.model.schemas;

public final class LikeSchema {
    public static final String TABLE_NAME = "likes";
    public static final String USER_ID = UserSchema.ID;
    public static final String POST_ID = PostSchema.ID;

    // This class shall not be instantiated
    private LikeSchema() {}
}
