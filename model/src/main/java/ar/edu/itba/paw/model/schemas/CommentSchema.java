package ar.edu.itba.paw.model.schemas;

public final class CommentSchema {
    public static final String TABLE_NAME = "comments";
    public static final String ID_SEQUENCE_GENERATOR = "comments_commentid_seq";

    public static final String ID = "commentid";
    public static final String USER_ID = UserSchema.ID;
    public static final String POST_ID = PostSchema.ID;
    public static final String CONTENT = "content";

    // This class shall not be instantiated
    private CommentSchema() {}
}
