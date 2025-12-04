package ar.edu.itba.paw.model.schemas;

public final class OpenPositionQuestionSchema {
    public static final String TABLE_NAME = "open_position_questions";
    public static final String ID_SEQUENCE_GENERATOR = "open_position_questions_questionid_seq";

    public static final String ID = "questionid";
    public static final String POSITION_ID = "positionid";
    public static final String QUESTION = "question";

    // This class shall not be instantiated
    private OpenPositionQuestionSchema() {}
}
