package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventEnrollmentDao;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.EventEnrollment;
import ar.edu.itba.paw.model.Event;
import ar.edu.itba.paw.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventEnrollmentJpaDao implements EventEnrollmentDao {
    @PersistenceContext private EntityManager em;

    @Override
    public void enrollUser(final Event event, final User user) {
        final EventEnrollment enrollment = new EventEnrollment(event, user, Status.PENDING);
        em.persist(enrollment);
    }

    @Override
    public void unenrollUser(final Event event, final User user) {
        final String deleteHql = "DELETE FROM EventEnrollment ee WHERE ee.event = :event AND ee.user = :user";
        em.createQuery(deleteHql).setParameter("event", event).setParameter("user", user).executeUpdate();
    }

    @Override
    public boolean isUserEnrolled(final Event event, final User user) {
        final String hql = "SELECT COUNT(ee) FROM EventEnrollment ee WHERE ee.event = :event AND ee.user = :user";
        return em.createQuery(hql, Long.class)
                .setParameter("event", event)
                .setParameter("user", user)
                .getSingleResult() > 0;
    }

    @Override
    public List<EventEnrollment> getEnrollments(final Event event) {
        final String hql = "FROM EventEnrollment ee JOIN FETCH ee.user WHERE ee.event = :event";
        return em.createQuery(hql, EventEnrollment.class).setParameter("event", event).getResultList();
    }

    @Override
    public long getEnrollmentCount(final Event event) {
        final String hql = "SELECT COUNT(ee) FROM EventEnrollment ee WHERE ee.event = :event";
        return em.createQuery(hql, Long.class).setParameter("event", event).getSingleResult();
    }

    @Override
    public List<EventEnrollment> getAllEventsUserEnrolled(final User user) {
        final String hql = "FROM EventEnrollment ee JOIN FETCH ee.event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE ee.user = :user";
        return em.createQuery(hql, EventEnrollment.class).setParameter("user", user).getResultList();
    }

    @Override
    public void updateEnrollmentStatus(final Event event, final User user, final Status status) {
        final String updateHql = "UPDATE EventEnrollment ee SET ee.status = :status WHERE ee.event = :event AND ee.user = :user";
        em.createQuery(updateHql)
                .setParameter("status", status)
                .setParameter("event", event)
                .setParameter("user", user)
                .executeUpdate();
    }

    @Override
    public Optional<Status> getEnrollmentStatus(final Event event, final User user) {
        final String hql = "SELECT ee.status FROM EventEnrollment ee WHERE ee.event = :event AND ee.user = :user";
        return em.createQuery(hql, Status.class)
                .setParameter("event", event)
                .setParameter("user", user)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<EventEnrollment> getEnrollmentsByStatus(final Event event, final Status status) {
        final String hql = "FROM EventEnrollment ee WHERE ee.event = :event AND ee.status = :status";
        return em.createQuery(hql, EventEnrollment.class)
                .setParameter("event", event)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public Map<Status, Long> getEnrollmentCountsByStatus(final Event event) {
        final String hql = "SELECT ee.status, COUNT(ee) FROM EventEnrollment ee WHERE ee.event = :event GROUP BY ee.status";
        return em.createQuery(hql, Object[].class)
                .setParameter("event", event)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                    result -> (Status) result[0],
                    result -> (Long) result[1]
                ));
    }

    @Override
    public void deleteAllEnrollmentsByEvent(final Event event) {
        final String deleteHql = "DELETE FROM EventEnrollment ee WHERE ee.event = :event";
        em.createQuery(deleteHql).setParameter("event", event).executeUpdate();
    }

    @Override
    public List<EventEnrollment> getAcceptedEnrollmentsForUpcomingEvents(final LocalDateTime startTime, final LocalDateTime endTime) {
        final String hql = "FROM EventEnrollment ee " +
                "JOIN FETCH ee.user " +
                "JOIN FETCH ee.event e " +
                "JOIN FETCH e.organization " +
                "WHERE ee.status = :status " +
                "AND e.eventDate >= :startTime " +
                "AND e.eventDate < :endTime";
        return em.createQuery(hql, EventEnrollment.class).setParameter("status", Status.ACCEPTED).setParameter("startTime", startTime).setParameter("endTime", endTime).getResultList();
    }
}
