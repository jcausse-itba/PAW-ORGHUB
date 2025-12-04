package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.schemas.OpenPositionQuestionSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = OpenPositionQuestionSchema.TABLE_NAME)
public class OpenPositionQuestion {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = OpenPositionQuestionSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = OpenPositionQuestionSchema.ID_SEQUENCE_GENERATOR, sequenceName = OpenPositionQuestionSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = OpenPositionQuestionSchema.ID)
    private Long questionID;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = OpenPositionQuestionSchema.POSITION_ID, nullable = false)
    private OpenPosition openPosition;

    @Column(name = OpenPositionQuestionSchema.QUESTION, length = Boundaries.OPEN_POSITION_QUESTION_MAX_LEN, nullable = false)
    private String question;

    public OpenPositionQuestion(final OpenPosition openPosition, final String question) {
        this.questionID = null;
        this.openPosition = openPosition;
        this.question = question;
    }
}
