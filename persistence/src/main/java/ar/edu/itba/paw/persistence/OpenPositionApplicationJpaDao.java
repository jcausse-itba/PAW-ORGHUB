package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.OpenPositionApplicationDao;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.OpenPositionApplication;
import ar.edu.itba.paw.model.OpenPositionAnswer;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class OpenPositionApplicationJpaDao implements OpenPositionApplicationDao {
    @PersistenceContext private EntityManager em;

    @Override
    public OpenPositionApplication apply(final OpenPosition position, final User user) {
        final OpenPositionApplication application = new OpenPositionApplication(position, user, Status.PENDING);
        em.persist(application);
        return application;
    }

    @Override
    public void updateStatus(final OpenPositionApplication application, final Status status) {
        application.setStatus(status);
    }

    @Override
    public List<OpenPositionApplication> getApplications(final OpenPosition position) {
        final String hql = "SELECT a FROM OpenPositionApplication a LEFT JOIN FETCH a.user WHERE a.openPosition = :position";
        return em.createQuery(hql, OpenPositionApplication.class).setParameter("position", position).getResultList();
    }

    @Override
    public Optional<OpenPositionApplication> getApplication(final long applicationID) {
        return Optional.ofNullable(em.find(OpenPositionApplication.class, applicationID));
    }

    @Override
    public Optional<Status> getStatus(final OpenPosition position, final User user) {
        final String hql = "SELECT a.status FROM OpenPositionApplication a WHERE a.openPosition = :position AND a.user = :user";
        return em.createQuery(hql, Status.class)
                .setParameter("position", position)
                .setParameter("user", user)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public void addAnswer(final OpenPositionApplication application, final OpenPositionQuestion question, final String answer) {
        final OpenPositionAnswer answerEntity = new OpenPositionAnswer(application, question, answer);
        em.persist(answerEntity);
    }

    @Override
    public List<OpenPositionAnswer> getAnswers(final OpenPositionApplication application) {
        final String hql = "FROM OpenPositionAnswer a WHERE a.application = :application";
        return em.createQuery(hql, OpenPositionAnswer.class).setParameter("application", application).getResultList();
    }

    @Override
    public void withdraw(final OpenPosition position, final User user) {
        // Delete all answers first (they reference applications)
        final String deleteAnswersHql = "DELETE FROM OpenPositionAnswer ans WHERE ans.application IN (SELECT a FROM OpenPositionApplication a WHERE a.openPosition = :position AND a.user = :user)";
        em.createQuery(deleteAnswersHql).setParameter("position", position).setParameter("user", user).executeUpdate();
        
        // Then delete the application
        final String deleteApplicationHql = "DELETE FROM OpenPositionApplication a WHERE a.openPosition = :position AND a.user = :user";
        em.createQuery(deleteApplicationHql).setParameter("position", position).setParameter("user", user).executeUpdate();
    }

    @Override
    public List<OpenPositionApplication> getUserApplications(final User user) {
        final String hql = "FROM OpenPositionApplication a WHERE a.user = :user";
        return em.createQuery(hql, OpenPositionApplication.class).setParameter("user", user).getResultList();
    }

    @Override
    public Map<Long, Status> getUserApplicationStatusMap(final User user) {
        final String hql = "SELECT a.openPosition.positionID, a.status FROM OpenPositionApplication a WHERE a.user = :user";
        return em.createQuery(hql, Object[].class)
                .setParameter("user", user)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                    result -> (Long) result[0],
                    result -> (Status) result[1]
                ));
    }

    @Override
    public List<OpenPositionApplication> getApplicationsByStatus(final OpenPosition position, final Status status) {
        final String hql = "SELECT a FROM OpenPositionApplication a LEFT JOIN FETCH a.user WHERE a.openPosition = :position AND a.status = :status";
        return em.createQuery(hql, OpenPositionApplication.class)
                .setParameter("position", position)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Map<Status, Long> getApplicationCountsByStatus(final OpenPosition position) {
        final String hql = "SELECT a.status, COUNT(a) FROM OpenPositionApplication a WHERE a.openPosition = :position GROUP BY a.status";
        return em.createQuery(hql, Object[].class)
                .setParameter("position", position)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                    result -> (Status) result[0],
                    result -> (Long) result[1]
                ));
    }

    @Override
    public void deleteAllApplicationsByPosition(final OpenPosition position) {
        // Delete all answers first (they reference applications)
        final String deleteAnswersHql = "DELETE FROM OpenPositionAnswer ans WHERE ans.application IN (SELECT a FROM OpenPositionApplication a WHERE a.openPosition = :position)";
        em.createQuery(deleteAnswersHql).setParameter("position", position).executeUpdate();
        
        // Then delete all applications
        final String deleteApplicationsHql = "DELETE FROM OpenPositionApplication a WHERE a.openPosition = :position";
        em.createQuery(deleteApplicationsHql).setParameter("position", position).executeUpdate();
    }
}
