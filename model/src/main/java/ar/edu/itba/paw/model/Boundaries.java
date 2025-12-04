package ar.edu.itba.paw.model;

public final class Boundaries {
    /* Users */
    public static final int USER_NAME_MAX_LEN = 100;
    public static final int PASSWORD_MAX_LEN = 150;

    /* Institutions */
    public static final int INST_NAME_MAX_LEN = 100;
    public static final int INST_DOMAIN_NAME_MAX_LEN = 100;

    /* Organizations */
    public static final int ORG_NAME_MAX_LEN = 100;
    public static final int ORG_DESCRIPTION_MAX_LEN = 500;

    /* Content (posts, events, etc.) */
    public static final int CONTENT_TITLE_MAX_LEN = 150;
    public static final int CONTENT_BODY_MAX_LEN = 5000;
    public static final int CONTENT_COMMENT_MAX_LEN = 500;
    public static final int CONTENT_MAX_TAGS = 3;

    /* Tags */
    public static final int TAG_MAX_LEN = 50;

    /* Open Positions */
    public static final int OPEN_POSITION_DESCRIPTION_MAX_LEN = 5000;
    public static final int OPEN_POSITION_REQUIREMENTS_MAX_LEN = 5000;
    public static final int OPEN_POSITION_WORKHOURS_MAX_LEN = 150;
    public static final int OPEN_POSITION_QUESTION_MAX_LEN = 150;
    public static final int OPEN_POSITION_ANSWER_MAX_LEN = 5000;

    /* Organigrams */
    public static final int ORGANIGRAM_POSITION_NAME_MAX_LEN = 150;
    public static final int ORGANIGRAM_DESCRIPTION_MAX_LEN = 500;

    /* Miscellaneous */
    public static final int URL_MAX_LEN = 150;
    public static final int EMAIL_MAX_LEN = 100;
    public static final int ENUM_CONSTANT_MAX_LEN = 20;

    /* Pagination */
    public static final int PAGE_SIZE = 20;

    private Boundaries() {}
}
