package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.EventDao;
import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.schemas.EventSchema;
import ar.edu.itba.paw.model.schemas.EventEnrollmentSchema;
import ar.edu.itba.paw.model.schemas.EventTagSchema;
import ar.edu.itba.paw.model.schemas.OrganizationSchema;
import ar.edu.itba.paw.model.schemas.TagSchema;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class EventJpaDao implements EventDao {
    @PersistenceContext private EntityManager em;

    private static class NativeQueries {
        private static String FIND_ALL_FOR_INSTITUTION_ORDERED(final OrderCriteria order) {
            return
            "SELECT e." + EventSchema.ID + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                        " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ALL_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT e." + EventSchema.ID + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " WHERE e." + EventSchema.ORG_ID + " IN (:orgIDList)" +
                    " ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                    " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ANY_TAG_ORDERED(final OrderCriteria order) {
            return
            "SELECT DISTINCT e." + EventSchema.ID + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " INNER JOIN " + EventTagSchema.TABLE_NAME + " AS et ON e." + EventSchema.ID + " = et." + EventTagSchema.EVENT_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " AND et." + EventTagSchema.TAG_ID + " IN (:tagIDList)" +
                            " ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }

        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_events AS (" +
                "SELECT DISTINCT " + EventTagSchema.EVENT_ID + " FROM " + EventTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A2, required_tags " +
                    "WHERE A1." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID +
                            " AND A3." + EventTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT e." + EventSchema.ID + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " INNER JOIN matching_events AS me ON e." + EventSchema.ID + " = me." + EventTagSchema.EVENT_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_POPULAR_FOR_INSTITUTION(final OrderCriteria order) {
            return
            "SELECT e." + EventSchema.ID +
                " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                " LEFT JOIN (SELECT " + EventEnrollmentSchema.EVENT_ID + ", COUNT(*) as enrollment_count" +
                    " FROM " + EventEnrollmentSchema.TABLE_NAME +
                    " GROUP BY " + EventEnrollmentSchema.EVENT_ID + ") AS enrollments" +
                    " ON e." + EventSchema.ID + " = enrollments." + EventEnrollmentSchema.EVENT_ID +
                " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID AND e." + EventSchema.EVENT_DATE + " >= NOW()" +
                    " ORDER BY COALESCE(enrollment_count, 0) DESC, e." + EventSchema.DATETIME + " " + order.toSQL() +
                    " LIMIT :limit";
        }

        private static String FIND_POPULAR_ANY_TAG(final OrderCriteria order) {
            return
            "SELECT e." + EventSchema.ID + " FROM (" +
                "SELECT DISTINCT e." + EventSchema.ID + ", COALESCE(enrollments.enrollment_count, 0) AS enrollment_count, e." + EventSchema.DATETIME + 
                " FROM " + EventSchema.TABLE_NAME + " AS e" +
                    " INNER JOIN " + EventTagSchema.TABLE_NAME + " AS et ON e." + EventSchema.ID + " = et." + EventTagSchema.EVENT_ID +
                        " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " LEFT JOIN (SELECT " + EventEnrollmentSchema.EVENT_ID + ", COUNT(*) as enrollment_count" +
                        " FROM " + EventEnrollmentSchema.TABLE_NAME +
                        " GROUP BY " + EventEnrollmentSchema.EVENT_ID + ") AS enrollments" +
                        " ON e." + EventSchema.ID + " = enrollments." + EventEnrollmentSchema.EVENT_ID +
                            " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " AND e." + EventSchema.EVENT_DATE + " >= NOW()" +
                            " AND et." + EventTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS e " +
                "ORDER BY e.enrollment_count DESC, e." + EventSchema.DATETIME + " " + order.toSQL() +
                " LIMIT :limit";
        }

        private static String FIND_POPULAR_ALL_TAGS(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_events AS (" +
                "SELECT DISTINCT " + EventTagSchema.EVENT_ID + " FROM " + EventTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID +
                            " AND A3." + EventTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT e." + EventSchema.ID + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON e." + EventSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                " LEFT JOIN (SELECT " + EventEnrollmentSchema.EVENT_ID + ", COUNT(*) as enrollment_count" +
                    " FROM " + EventEnrollmentSchema.TABLE_NAME +
                    " GROUP BY " + EventEnrollmentSchema.EVENT_ID + ") AS enrollments" +
                    " ON e." + EventSchema.ID + " = enrollments." + EventEnrollmentSchema.EVENT_ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " AND e." + EventSchema.EVENT_DATE + " >= NOW()" +
                        " AND e." + EventSchema.ID + " IN (SELECT " + EventTagSchema.EVENT_ID + " FROM matching_events)" +
                            " ORDER BY COALESCE(enrollment_count, 0) DESC, e." + EventSchema.DATETIME + " " + order.toSQL() +
                            " LIMIT :limit";
        }

        private static String FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT e." + EventSchema.ID + " FROM (" +
                "SELECT DISTINCT e." + EventSchema.ID + ", e." + EventSchema.DATETIME + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                    " INNER JOIN " + EventTagSchema.TABLE_NAME + " AS et ON e." + EventSchema.ID + " = et." + EventTagSchema.EVENT_ID +
                        " WHERE e." + EventSchema.ORG_ID + " IN (:orgIDList)" +
                        " AND et." + EventTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS e " +
                "ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                " OFFSET :offset LIMIT :limit";
        }

        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_events AS (" +
                "SELECT DISTINCT " + EventTagSchema.EVENT_ID + " FROM " + EventTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + EventTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + EventTagSchema.EVENT_ID + " = A2." + EventTagSchema.EVENT_ID +
                            " AND A3." + EventTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT e." + EventSchema.ID + " FROM (" +
                "SELECT e." + EventSchema.ID + ", e." + EventSchema.DATETIME + " FROM " + EventSchema.TABLE_NAME + " AS e" +
                    " INNER JOIN matching_events AS me ON e." + EventSchema.ID + " = me." + EventTagSchema.EVENT_ID +
                        " WHERE e." + EventSchema.ORG_ID + " IN (:orgIDList)" +
            ") AS e " +
                "ORDER BY e." + EventSchema.DATETIME + " " + order.toSQL() + ", e." + EventSchema.ID + " " + order.toSQL() +
                " OFFSET :offset LIMIT :limit";
        }
    }

    private List<Long> executeNativeQuery(final String sql, final Map<String, Object> params) {
        final Query query = em.createNativeQuery(sql);
        params.forEach(query::setParameter);
        @SuppressWarnings("unchecked") final List<Object> results = query.getResultList();
        return results.stream().map(obj -> ((Number) obj).longValue()).toList();
    }

    @Override
    public Optional<Event> findByID(final long eventID) {
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID = :eventID";
        return em.createQuery(hql, Event.class)
                .setParameter("eventID", eventID)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Event> findAllForInstitution(final Institution inst, final OrderCriteria order, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> eventIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_INSTITUTION_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }

    @Override
    public List<Event> findAllForOrganizations(final List<Organization> orgs, final OrderCriteria order, final int pageNum) {
        if (orgs == null || orgs.isEmpty()) {
            return List.of();
        }
        final List<Long> orgIDs = orgs.stream().map(Organization::getOrgID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "orgIDList", orgIDs,
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> eventIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_ORGANIZATIONS_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }

    @Override
    public List<Event> findAllForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        final List<Long> tagIDs = tags.stream().map(Tag::getTagID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "tagIDList", tagIDs,
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> eventIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }

    @Override
    public Event create(final Organization org, final String title, final String desc, final String location, final LocalDateTime eventDate, final LocalDateTime dateTime, final Set<Tag> tags, final ar.edu.itba.paw.model.image.Image image) {
        final Set<Tag> limitedTags = tags != null && tags.size() > Boundaries.CONTENT_MAX_TAGS
            ? tags.stream().limit(Boundaries.CONTENT_MAX_TAGS).collect(Collectors.toSet())
            : tags;
        final Event event = new Event(org, title, desc, location, eventDate, dateTime, limitedTags);
        event.setImage(image);
        em.persist(event);
        return event;
    }

    @Override
    public void delete(final Event event) {
        em.remove(event);
    }

    @Override
    public List<Event> findPopularEventsForInstitution(final Institution inst, final OrderCriteria order, final int limit) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "limit", limit
        ));
        final List<Long> eventIDs = executeNativeQuery(NativeQueries.FIND_POPULAR_FOR_INSTITUTION(order), nativeQueryParameters);
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }

    @Override
    public List<Event> findPopularEventsForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        final List<Long> tagIDs = tags.stream().map(Tag::getTagID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "tagIDList", tagIDs,
            "limit", limit
        ));
        final List<Long> eventIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_POPULAR_ANY_TAG(order) : NativeQueries.FIND_POPULAR_ALL_TAGS(order),
            nativeQueryParameters
        );
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }

    @Override
    public List<Event> findAllForOrganizationsAndTags(final List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
        if (orgs == null || orgs.isEmpty() || tags == null || tags.isEmpty()) {
            return List.of();
        }
        final List<Long> orgIDs = orgs.stream().map(Organization::getOrgID).toList();
        final List<Long> tagIDs = tags.stream().map(Tag::getTagID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "orgIDList", orgIDs,
            "tagIDList", tagIDs,
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> eventIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM Event e JOIN FETCH e.organization LEFT JOIN FETCH e.image WHERE e.eventID IN (:eventIDs) ORDER BY e.datetime " + order.toSQL() + ", e.eventID " + order.toSQL();
        return em.createQuery(hql, Event.class).setParameter("eventIDs", eventIDs).getResultList();
    }
}
