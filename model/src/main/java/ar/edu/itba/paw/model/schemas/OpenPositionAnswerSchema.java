package ar.edu.itba.paw.model.schemas;

public final class OpenPositionAnswerSchema {
    public static final String TABLE_NAME = "open_position_answers";
    public static final String ID_SEQUENCE_GENERATOR = "open_position_answers_answerid_seq";

    public static final String ID = "answerid";
    public static final String APPLICATION_ID = "applicationid";
    public static final String QUESTION_ID = "questionid";
    public static final String ANSWER = "answer";

    // This class shall not be instantiated
    private OpenPositionAnswerSchema() {}
}