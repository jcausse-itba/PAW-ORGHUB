package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.OpenPositionAnswerSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OpenPositionAnswerSchema.TABLE_NAME)
public class OpenPositionAnswer {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OpenPositionAnswerSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OpenPositionAnswerSchema.ID_SEQUENCE_GENERATOR, sequenceName = OpenPositionAnswerSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OpenPositionAnswerSchema.ID)
    private Long answerID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionAnswerSchema.APPLICATION_ID, nullable = false)
    private OpenPositionApplication application;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionAnswerSchema.QUESTION_ID, nullable = false)
    private OpenPositionQuestion question;

    @Column(name = OpenPositionAnswerSchema.ANSWER, length = Boundaries.OPEN_POSITION_ANSWER_MAX_LEN, nullable = false)
    private String answer;

    public OpenPositionAnswer(final OpenPositionApplication application, final OpenPositionQuestion question, final String answer) {
        this.answerID = null;
        this.application = application;
        this.question = question;
        this.answer = answer;
    }
}
