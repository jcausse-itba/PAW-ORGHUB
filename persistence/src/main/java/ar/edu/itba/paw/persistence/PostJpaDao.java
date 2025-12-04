package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PostDao;
import ar.edu.itba.paw.interfaces.enums.TagFilterCriteria;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.interfaces.enums.OrderCriteria;
import ar.edu.itba.paw.model.schemas.*;
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
public class PostJpaDao implements PostDao {
    @PersistenceContext private EntityManager em;

    private static class NativeQueries {
        private static String FIND_ALL_FOR_INSTITUTION_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
                        " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ALL_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " WHERE p." + PostSchema.ORG_ID + " IN (:orgIDList)" +
                    " ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
                    " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_ANY_TAG_ORDERED(final OrderCriteria order) {
            return
            "SELECT DISTINCT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + PostTagSchema.TABLE_NAME + " AS pt ON p." + PostSchema.ID + " = pt." + PostTagSchema.POST_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                        " AND pt." + PostTagSchema.TAG_ID + " IN (:tagIDList)" +
                            " ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }
        
        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_posts AS (" +
                "SELECT DISTINCT " + PostTagSchema.POST_ID + " FROM " + PostTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A2, required_tags " +
                    "WHERE A1." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID +
                            " AND A3." + PostTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " INNER JOIN matching_posts AS mp ON p." + PostSchema.ID + " = mp." + PostTagSchema.POST_ID +
                    " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                        " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
                            " OFFSET :offset LIMIT :limit";
        }

        private static String FIND_POPULAR_FOR_INSTITUTION(final OrderCriteria order) {
            return
            "SELECT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                    " ORDER BY COALESCE(p." + PostSchema.LIKES + ", 0) DESC, p." + PostSchema.DATETIME + " " + order.toSQL() + " LIMIT :limit";
        }

        private static String FIND_POPULAR_ANY_TAG(final OrderCriteria order) {
            return
            "SELECT p." + PostSchema.ID + " FROM (" +
                "SELECT DISTINCT p." + PostSchema.ID + ", COALESCE(p." + PostSchema.LIKES + ", 0) AS likes, p." + PostSchema.DATETIME + 
                " FROM " + PostSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN " + PostTagSchema.TABLE_NAME + " AS pt ON p." + PostSchema.ID + " = pt." + PostTagSchema.POST_ID +
                        " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                            " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                            " AND pt." + PostTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS p " +
                "ORDER BY p.likes DESC, p." + PostSchema.DATETIME + " " + order.toSQL() +
                " LIMIT :limit";
        }

        private static String FIND_POPULAR_ALL_TAGS(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_posts AS (" +
                "SELECT DISTINCT " + PostTagSchema.POST_ID + " FROM " + PostTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID +
                            " AND A3." + PostTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + PostSchema.ID + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                " INNER JOIN " + OrganizationSchema.TABLE_NAME + " AS o ON p." + PostSchema.ORG_ID + " = o." + OrganizationSchema.ID +
                    " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                    " AND p." + PostSchema.ID + " IN (SELECT " + PostTagSchema.POST_ID + " FROM matching_posts)" +
                        " ORDER BY COALESCE(p." + PostSchema.LIKES + ", 0) DESC, p." + PostSchema.DATETIME + " " + order.toSQL() +
                        " LIMIT :limit";
        }

        private static final String FIND_COMMENTS_ORDERED =
            "SELECT c." + CommentSchema.ID + " FROM " + CommentSchema.TABLE_NAME + " AS c" +
                " WHERE c." + CommentSchema.POST_ID + " = :postID" +
                " ORDER BY c." + CommentSchema.ID +
                " OFFSET :offset LIMIT :limit";

        private static String FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "SELECT p." + PostSchema.ID + " FROM (" +
                "SELECT DISTINCT p." + PostSchema.ID + ", p." + PostSchema.DATETIME + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN " + PostTagSchema.TABLE_NAME + " AS pt ON p." + PostSchema.ID + " = pt." + PostTagSchema.POST_ID +
                        " WHERE p." + PostSchema.ORG_ID + " IN (:orgIDList)" +
                        " AND pt." + PostTagSchema.TAG_ID + " IN (:tagIDList)" +
            ") AS p " +
                "ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
                " OFFSET :offset LIMIT :limit";
        }

        // A/B = pi_a(A) - pi_a((pi_a(A) x B) - A)
        private static String FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(final OrderCriteria order) {
            return
            "WITH required_tags AS (" +
                "SELECT DISTINCT " + TagSchema.ID + " FROM " + TagSchema.TABLE_NAME + " WHERE " + TagSchema.ID + " IN (:tagIDList)" +
            "), matching_posts AS (" +
                "SELECT DISTINCT " + PostTagSchema.POST_ID + " FROM " + PostTagSchema.TABLE_NAME + " AS A1 " +
                "WHERE NOT EXISTS(" +
                    "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A2 " +
                    "CROSS JOIN required_tags " +
                    "WHERE A1." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID + " AND NOT EXISTS(" +
                        "SELECT 1 FROM " + PostTagSchema.TABLE_NAME + " AS A3 " +
                            "WHERE A3." + PostTagSchema.POST_ID + " = A2." + PostTagSchema.POST_ID +
                            " AND A3." + PostTagSchema.TAG_ID + " = required_tags." + TagSchema.ID +
                    ")" +
                ")" +
            ") " +
            "SELECT p." + PostSchema.ID + " FROM (" +
                "SELECT p." + PostSchema.ID + ", p." + PostSchema.DATETIME + " FROM " + PostSchema.TABLE_NAME + " AS p" +
                    " INNER JOIN matching_posts AS mp ON p." + PostSchema.ID + " = mp." + PostTagSchema.POST_ID +
                        " WHERE p." + PostSchema.ORG_ID + " IN (:orgIDList)" +
            ") AS p " +
                "ORDER BY p." + PostSchema.DATETIME + " " + order.toSQL() + ", p." + PostSchema.ID + " " + order.toSQL() +
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
    public Optional<Post> findByID(final long postID) {
        return Optional.ofNullable(em.find(Post.class, postID));
    }

    @Override
    public List<Post> findAllForInstitution(final Institution inst, final OrderCriteria order, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> postIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_INSTITUTION_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }

    @Override
    public List<Post> findAllForOrganizations(final List<Organization> orgs, final OrderCriteria order, final int pageNum) {
        if (orgs == null || orgs.isEmpty()) {
            return List.of();
        }
        final List<Long> orgIDs = orgs.stream().map(Organization::getOrgID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
                "orgIDList", orgIDs,
                "offset", pageNum * Boundaries.PAGE_SIZE,
                "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> postIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_ORGANIZATIONS_ORDERED(order), nativeQueryParameters);
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }

    @Override
    public List<Post> findAllForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
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
        final List<Long> postIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }

    @Override
    public Post create(final Organization org, final String title, final String body, final LocalDateTime dateTime, final Set<Tag> tags) {
        final Set<Tag> limitedTags = tags != null && tags.size() > Boundaries.CONTENT_MAX_TAGS
            ? tags.stream().limit(Boundaries.CONTENT_MAX_TAGS).collect(Collectors.toSet())
            : tags;
        final Post post = new Post(org, title, body, dateTime, limitedTags);
        em.persist(post);
        return post;
    }

    @Override
    public void delete(final Post post) {
        em.remove(post);
    }

    @Override
    public List<Comment> getComments(final Post post, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "postID", post.getPostID(),
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> commentIDs = executeNativeQuery(NativeQueries.FIND_COMMENTS_ORDERED, nativeQueryParameters);
        final String hql = "FROM Comment c WHERE c.commentID IN (:commentIDs)";
        return em.createQuery(hql, Comment.class).setParameter("commentIDs", commentIDs).getResultList();
    }

    @Override
    public List<Post> findPopularPostsForInstitution(final Institution inst, final OrderCriteria order, final int limit) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "limit", limit
        ));
        final List<Long> postIDs = executeNativeQuery(NativeQueries.FIND_POPULAR_FOR_INSTITUTION(order), nativeQueryParameters);
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }

    @Override
    public List<Post> findPopularPostsForTags(final Institution inst, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int limit) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        final List<Long> tagIDs = tags.stream().map(Tag::getTagID).toList();
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "tagIDList", tagIDs,
            "limit", limit
        ));
        final List<Long> postIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_POPULAR_ANY_TAG(order) : NativeQueries.FIND_POPULAR_ALL_TAGS(order),
            nativeQueryParameters
        );
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }

    @Override
    public List<Post> findAllForOrganizationsAndTags(final List<Organization> orgs, final List<Tag> tags, final TagFilterCriteria filter, final OrderCriteria order, final int pageNum) {
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
        final List<Long> postIDs = executeNativeQuery(
            filter == TagFilterCriteria.ANY ? NativeQueries.FIND_ANY_TAG_FOR_ORGANIZATIONS_ORDERED(order) : NativeQueries.FIND_ALL_TAGS_FOR_ORGANIZATIONS_ORDERED(order),
            nativeQueryParameters
        );
        final String hql = "FROM Post p JOIN FETCH p.organization WHERE p.postID IN (:postIDs) ORDER BY p.datetime " + order.toSQL() + ", p.postID " + order.toSQL();
        return em.createQuery(hql, Post.class).setParameter("postIDs", postIDs).getResultList();
    }
}
