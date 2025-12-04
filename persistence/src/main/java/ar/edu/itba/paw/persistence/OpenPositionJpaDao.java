package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.OpenPositionDao;
import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.Boundaries;
import ar.edu.itba.paw.model.OpenPosition;
import ar.edu.itba.paw.model.OpenPositionQuestion;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import ar.edu.itba.paw.model.schemas.OpenPositionApplicationSchema;
import ar.edu.itba.paw.model.schemas.OpenPositionSchema;
import ar.edu.itba.paw.model.schemas.OpenPositionTagSchema;
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
public class OpenPositionJpaDao implements OpenPositionDao {
    @PersistenceContext private EntityManager em;

    private static class NativeQueries {

        private static String FIND_ALL_FOR_INSTITUTION_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + OpenPositionSchema.ID + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
                        " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ALL_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + OpenPositionSchema.ID + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                " WHERE p." + OpenPositionSchema.ORG_ID + " IN (:orgIDList)" +
                    " ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
                    " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ANY_TAG_ORDERED(final OrderCriteria order) {
            return
            "SELECT DISTINCT p." + OpenPositionSchema.ID + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OpenPositionTagSchema.TABLE_NAME + " AS pt ON p." + OpenPositionSchema.ID + " = pt." + OpenPositionTagSchema.POSITION_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " AND pt." + OpenPositionTagSchema.TAG_ID + " IN (:tagIDList)" +
                            " ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }

        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_positions AS (" +
                "SELECT DISTINCT " + OpenPositionTagSchema.POSITION_ID + " FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A2, required_tags " +
                    "WHERE A1." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID +
                            " AND A3." + OpenPositionTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + OpenPositionSchema.ID + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                " INNER JOIN matching_positions AS mp ON p." + OpenPositionSchema.ID + " = mp." + OpenPositionTagSchema.POSITION_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_POPULAR_FOR_INSTITUTION(final OrderCriteria order) {
            return
            "SELECT p." + OpenPositionSchema.ID + " " +
                "FROM " + OpenPositionSchema.TABLE_NAME + " AS p " +
                "INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID + " " +
                "LEFT JOIN (SELECT " + OpenPositionApplicationSchema.POSITION_ID + ", COUNT(*) as application_count " +
                "FROM " + OpenPositionApplicationSchema.TABLE_NAME + " " +
                "GROUP BY " + OpenPositionApplicationSchema.POSITION_ID + ") AS applications " +
                "ON p." + OpenPositionSchema.ID + " = applications." + OpenPositionApplicationSchema.POSITION_ID + " " +
                "WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID " +
                "ORDER BY COALESCE(application_count, 0) DESC, p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + " " +
                "LIMIT :limit";
        }

        private static String FIND_POPULAR_ANY_TAG(final OrderCriteria order) {
            return
            "SELECT p." + OpenPositionSchema.ID + " FROM (" +
                "SELECT DISTINCT p." + OpenPositionSchema.ID + ", COALESCE(applications.application_count, 0) AS application_count, p." + OpenPositionSchema.DATETIME + 
                " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN " + OpenPositionTagSchema.TABLE_NAME + " AS pt ON p." + OpenPositionSchema.ID + " = pt." + OpenPositionTagSchema.POSITION_ID +
                        " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " LEFT JOIN (SELECT " + OpenPositionApplicationSchema.POSITION_ID + ", COUNT(*) as application_count " +
                    "FROM " + OpenPositionApplicationSchema.TABLE_NAME + " " +
                    "GROUP BY " + OpenPositionApplicationSchema.POSITION_ID + ") AS applications " +
                    "ON p." + OpenPositionSchema.ID + " = applications." + OpenPositionApplicationSchema.POSITION_ID +
                            " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " AND pt." + OpenPositionTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS p " +
                "ORDER BY p.application_count DESC, p." + OpenPositionSchema.DATETIME + " " + order.toSQL() +
                " LIMIT :limit";
        }

        private static String FIND_POPULAR_ALL_TAGS(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_positions AS (" +
                "SELECT DISTINCT " + OpenPositionTagSchema.POSITION_ID + " FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID +
                            " AND A3." + OpenPositionTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + OpenPositionSchema.ID + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + OpenPositionSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                " LEFT JOIN (SELECT " + OpenPositionApplicationSchema.POSITION_ID + ", COUNT(*) as application_count " +
                "FROM " + OpenPositionApplicationSchema.TABLE_NAME + " " +
                "GROUP BY " + OpenPositionApplicationSchema.POSITION_ID + ") AS applications " +
                "ON p." + OpenPositionSchema.ID + " = applications." + OpenPositionApplicationSchema.POSITION_ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " AND p." + OpenPositionSchema.ID + " IN (SELECT " + OpenPositionTagSchema.POSITION_ID + " FROM matching_positions)" +
                            " ORDER BY COALESCE(application_count, 0) DESC, p." + OpenPositionSchema.DATETIME + " " + order.toSQL() +
                            " LIMIT :limit";
        }

        private static String FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + OpenPositionSchema.ID + " FROM (" +
                "SELECT DISTINCT p." + OpenPositionSchema.ID + ", p." + OpenPositionSchema.DATETIME + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN " + OpenPositionTagSchema.TABLE_NAME + " AS pt ON p." + OpenPositionSchema.ID + " = pt." + OpenPositionTagSchema.POSITION_ID +
                        " WHERE p." + OpenPositionSchema.ORG_ID + " IN (:orgIDList)" +
                        " AND pt." + OpenPositionTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS p " +
                "ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
                " OFFSET :offset LIMIT :limit";
        }

        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_positions AS (" +
                "SELECT DISTINCT " + OpenPositionTagSchema.POSITION_ID + " FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + OpenPositionTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + OpenPositionTagSchema.POSITION_ID + " = A2." + OpenPositionTagSchema.POSITION_ID +
                            " AND A3." + OpenPositionTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + OpenPositionSchema.ID + " FROM (" +
                "SELECT p." + OpenPositionSchema.ID + ", p." + OpenPositionSchema.DATETIME + " FROM " + OpenPositionSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN matching_positions AS mp ON p." + OpenPositionSchema.ID + " = mp." + OpenPositionTagSchema.POSITION_ID +
                        " WHERE p." + OpenPositionSchema.ORG_ID + " IN (:orgIDList)" +
            ") AS p " +
                "ORDER BY p." + OpenPositionSchema.DATETIME + " " + order.toSQL() + ", p." + OpenPositionSchema.ID + " " + order.toSQL() +
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
    public Optional<OpenPosition> findByID(final long positionID) {
        return Optional.ofNullable(em.find(OpenPosition.class, positionID));
    }

    @Override
    public List<OpenPosition> findAllForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
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
        final List<Long> positionIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }

    @Override
    public OpenPosition create(final Organization org, final String title, final String description, final String requirements, final String workhours, final LocalDateTime datetime, final List<String> questions, final Organigram organigram, final Set<Tag> tags) {
        final Set<Tag> limitedTags = tags != null && tags.size() > Boundaries.CONTENT_MAX_TAGS
            ? tags.stream().limit(Boundaries.CONTENT_MAX_TAGS).collect(Collectors.toSet())
            : tags;
        final OpenPosition entity = new OpenPosition(
            org,
            title,
            description,
            requirements,
            workhours,
            datetime,
            organigram,
            limitedTags
        );
        
        em.persist(entity);
        em.flush();
        
        if (questions != null && !questions.isEmpty()) {
            questions.forEach(question -> {
                final OpenPositionQuestion questionEntity = new OpenPositionQuestion(entity, question);
                em.persist(questionEntity);
            });
        }
        
        return entity;
    }

    @Override
    public void addQuestion(final OpenPosition position, final String question) {
        final OpenPositionQuestion questionEntity = new OpenPositionQuestion(position, question);
        em.persist(questionEntity);
    }

    @Override
    public List<OpenPositionQuestion> getQuestions(final OpenPosition position) {
        final String hql = "FROM OpenPositionQuestion q WHERE q.openPosition = :position";
        return em.createQuery(hql, OpenPositionQuestion.class).setParameter("position", position).getResultList();
    }

    @Override
    public Optional<OpenPositionQuestion> getQuestionByID(final long questionID) {
        return Optional.ofNullable(em.find(OpenPositionQuestion.class, questionID));
    }

    @Override
    public List<OpenPosition> findAllForInstitution(final Institution inst, final OrderCriteria order, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
                "institutionID", inst.getInstitutionID(),
                "offset", pageNum * Boundaries.PAGE_SIZE,
                "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> positionIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_INSTITUTION_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }

    @Override
    public List<OpenPosition> findAllForOrganizations(final List<Organization> orgs, final OrderCriteria order, final int pageNum) {
        if (orgs == null || orgs.isEmpty()) {
            return List.of();
        }
        final List<Long> orgIDs = orgs.stream().map(Organization::getOrgID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
                "orgIDList", orgIDs,
                "offset", pageNum * Boundaries.PAGE_SIZE,
                "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> positionIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_ORGANIZATIONS_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }

    @Override
    public List<OpenPosition> findPopularOpenPositionsForInstitution(final Institution inst, final OrderCriteria order, final int limit) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
                "institutionID", inst.getInstitutionID(),
                "limit", limit
        ));
        final List<Long> positionIDs = executeNativeQuery(NativeQueries.FIND_POPULAR_FOR_INSTITUTION(order), nativeQueryParameters);
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }

    @Override
    public List<OpenPosition> findPopularOpenPositionsForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        final List<Long> tagIDs = tags.stream().map(Tag::getTagID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "tagIDList", tagIDs,
            "limit", limit
        ));
        final List<Long> positionIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_POPULAR_ANY_TAG(order) : NativeQueries.FIND_POPULAR_ALL_TAGS(order),
            nativeQueryParameters
        );
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }

    @Override
    public void deleteAllQuestionsByPosition(final OpenPosition position) {
        final String deleteHql = "DELETE FROM OpenPositionQuestion q WHERE q.openPosition = :position";
        em.createQuery(deleteHql).setParameter("position", position).executeUpdate();
    }

    @Override
    public void delete(final OpenPosition position) {
        em.remove(position);
    }

    @Override
    public List<OpenPosition> findAllForOrganizationsAndTags(final List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
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
        final List<Long> positionIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM OpenPosition p JOIN FETCH p.organization WHERE p.positionID IN (:positionIDs) ORDER BY p.datetime " + order.toSQL() + ", p.positionID " + order.toSQL();
        return em.createQuery(hql, OpenPosition.class).setParameter("positionIDs", positionIDs).getResultList();
    }


    @Override
    public List<OpenPosition> findActiveByOrganizationWithOrganigram(final Organization organization) {
        final String hql = "FROM OpenPosition p JOIN FETCH p.organigramPosition WHERE p.organization = :organization AND p.status = :status AND p.organigramPosition IS NOT NULL";
        return em.createQuery(hql, OpenPosition.class)
                .setParameter("organization", organization)
                .setParameter("status", OpenPositionStatus.ACTIVE)
                .getResultList();
    }
}
