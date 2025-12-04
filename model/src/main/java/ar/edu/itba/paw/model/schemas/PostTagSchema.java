package ar.edu.itba.paw.model.schemas;

public final class PostTagSchema {
    public static final String TABLE_NAME = "posts_tags";
    public static final String POST_ID = PostSchema.ID;
    public static final String TAG_ID = TagSchema.ID;

    // This class shall not be instantiated
    private PostTagSchema() {}
}
