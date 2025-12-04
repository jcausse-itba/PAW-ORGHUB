package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.schemas.UserSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = UserSchema.TABLE_NAME)
public class User {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = UserSchema.ID_SEQUENCE_GENERATOR)
    @SequenceGenerator(name = UserSchema.ID_SEQUENCE_GENERATOR, sequenceName = UserSchema.ID_SEQUENCE_GENERATOR, allocationSize = 1)
    @Column(name = UserSchema.ID)
    private Long userID;

    @EqualsAndHashCode.Include
    @Column(name = UserSchema.EMAIL, length = Boundaries.EMAIL_MAX_LEN, nullable = false, unique = true)
    private String userEmail;

    @Column(name = UserSchema.FIRST_NAME, length = Boundaries.USER_NAME_MAX_LEN)
    private String firstName;

    @Column(name = UserSchema.LAST_NAME, length = Boundaries.USER_NAME_MAX_LEN)
    private String lastName;

    @Column(name = UserSchema.ENC_PASS, length = Boundaries.PASSWORD_MAX_LEN, nullable = false)
    private String encodedPassword;

    @Setter
    @Column(name = UserSchema.PREF_LANG, length = Boundaries.ENUM_CONSTANT_MAX_LEN)
    @Enumerated(EnumType.STRING)
    private Language preferredLanguage;

    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = UserSchema.INSTITUTION_ID)
    private Institution institution;

    public User(
        final String userEmail,
        final String firstName,
        final String lastName,
        final String encodedPassword,
        final Language preferredLanguage,
        final Institution institution
    ) {
        this.userID = null;
        this.userEmail = userEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.encodedPassword = encodedPassword;
        this.preferredLanguage = preferredLanguage;
        this.institution = institution;
    }
}
