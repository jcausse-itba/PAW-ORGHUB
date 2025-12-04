package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.UserDao;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.enums.Language;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
public class UserJpaDao implements UserDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Optional<User> findByID(final long userID) {
        final String hql = "FROM User u JOIN FETCH u.institution WHERE u.userID = :userID";
        return em.createQuery(hql, User.class).setParameter("userID", userID).getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        final String hql = "FROM User u JOIN FETCH u.institution WHERE u.userEmail = :email";
        return em.createQuery(hql, User.class).setParameter("email", email).getResultList().stream().findFirst();
    }

    @Override
    public Optional<User> findWithoutInstitution(long userID) {
        return Optional.ofNullable(em.find(User.class, userID));
    }

    @Override
    public Optional<User> findWithoutInstitution(String email) {
        final String hql = "FROM User u WHERE u.userEmail = :email";
        return em.createQuery(hql, User.class).setParameter("email", email).getResultList().stream().findFirst();
    }

    @Override
    public User create(final String email, final String firstName, final String lastName, final String pass, final Language lang, final Institution inst) {
        final User u = new User(email, firstName, lastName, pass, lang, inst);
        em.persist(u);  // Sets the userID
        return u;
    }

    @Override
    public boolean existsEmail(final String email) {
        final String hql = "SELECT COUNT(u) FROM User u WHERE u.userEmail = :email";
        return em.createQuery(hql, Long.class).setParameter("email", email).getSingleResult() > 0;
    }

    @Override
    public void setPreferredLanguage(final User user, final Language language) {
        user.setPreferredLanguage(language);
    }
}
