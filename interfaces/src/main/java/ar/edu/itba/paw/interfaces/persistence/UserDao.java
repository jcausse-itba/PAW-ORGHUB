// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.User;

import java.util.Optional;

public interface UserDao {
    /**
     * Find a User via its ID.
     * @param userID    The ID of the User to be found.
     * @return          An Optional containing a User if the user was found. An empty Optional otherwise.
     */
    Optional<User> findByID(long userID);

    /**
     * Find a User via its unique email address.
     * @param email     The email of the User to be found.
     * @return          An Optional containing a User if the user was found. An empty Optional otherwise.
     */
    Optional<User> findByEmail(String email);

    /**
     * Find a User, but do not fetch their associated Institution.
     * @param userID    The ID of the User to be found.
     * @return          An Optional containing a User if the user was found. An empty Optional otherwise.
     */
    Optional<User> findWithoutInstitution(long userID);

    /**
     * Find a User, but do not fetch their associated Institution.
     * @param email     The email of the User to be found.
     * @return          An Optional containing a User if the user was found. An empty Optional otherwise.
     */
    Optional<User> findWithoutInstitution(String email);

    /**
     * Create a new user and get the User object that represents it.
     * @param email             User's email. Must be unique and not null.
     * @param firstName         User's first name.
     * @param lastName          User's last name.
     * @param pass              A String representing the user's password, encoded.
     * @param lang              A Language representing the user's preferred language.
     * @param inst              The Institution the user belongs to.
     * @return                  A User representing the created user.
     */
    User create(String email, String firstName, String lastName, String pass, Language lang, Institution inst);

    /**
     * Check whether an email is already taken by a user or not.
     * @param email             The email address to check.
     * @return                  Boolean indicating result.
     */
    boolean existsEmail(String email);

    /**
     * Update a user's preferred language.
     * @param user              The User whose preferred language is to be updated. Must not be null.
     * @param language          The Language to set as preferred. Must not be null.
     */
    void setPreferredLanguage(User user, Language language);
}
