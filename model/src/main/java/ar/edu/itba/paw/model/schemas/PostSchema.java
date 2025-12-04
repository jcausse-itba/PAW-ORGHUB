package ar.edu.itba.paw.model.schemas;

public final class PostSchema {
    public static final String TABLE_NAME = "posts";
    public static final String ID_SEQUENCE_GENERATOR = "posts_postid_seq";

    public static final String ID = "postid";
    public static final String ORG_ID = OrganizationSchema.ID;
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String LIKES = "likes";
    public static final String COMMENTS = "comments";
    public static final String DATETIME = "datetime";

    // This class shall not be instantiated
    private PostSchema() {}
}
