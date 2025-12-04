package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.OrganizationDao;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.schemas.OrganizationSchema;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OrganizationJpaDao implements OrganizationDao {
    @PersistenceContext private EntityManager em;

    private static class NativeQueries {
        private static final String FIND_ALL_ORDERED =
            "SELECT o." + OrganizationSchema.ID + " FROM " + OrganizationSchema.TABLE_NAME + " AS o" +
                " ORDER BY o." + OrganizationSchema.NAME +
                " OFFSET :offset LIMIT :limit";

        private static final String FIND_ALL_FOR_INSTITUTION_ORDERED =
            "SELECT o." + OrganizationSchema.ID + " FROM " + OrganizationSchema.TABLE_NAME + " AS o" +
                " WHERE o." + OrganizationSchema.INSTITUTION_ID + " = :institutionID" +
                " ORDER BY o." + OrganizationSchema.ID +
                " OFFSET :offset LIMIT :limit";
    }

    private List<Long> executeNativeQuery(final String sql, final Map<String, Object> params) {
        final Query query = em.createNativeQuery(sql);
        params.forEach(query::setParameter);
        @SuppressWarnings("unchecked") final List<Object> results = query.getResultList();
        return results.stream().map(obj -> ((Number) obj).longValue()).toList();
    }

    @Override
    public Optional<Organization> findByID(final long orgID) {
        return Optional.ofNullable(em.find(Organization.class, orgID));
    }

    @Override
    public Optional<Organization> findByEmail(final String email) {
        final String hql = "FROM Organization o WHERE o.orgEmail = :email";
        return em.createQuery(hql, Organization.class).setParameter("email", email).getResultList().stream().findFirst();
    }

    @Override
    public List<Organization> findAllForInstitution(final Institution inst, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> orgIDs = executeNativeQuery(NativeQueries.FIND_ALL_FOR_INSTITUTION_ORDERED, nativeQueryParameters);
        final String hql = "FROM Organization o WHERE o.orgID IN (:orgIDs)";
        return em.createQuery(hql, Organization.class).setParameter("orgIDs", orgIDs).getResultList();
    }

    @Override
    public List<Organization> findAll(final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> orgIDs = executeNativeQuery(NativeQueries.FIND_ALL_ORDERED, nativeQueryParameters);
        final String hql = "FROM Organization o WHERE o.orgID IN (:orgIDs)";
        return em.createQuery(hql, Organization.class).setParameter("orgIDs", orgIDs).getResultList();
    }

    @Override
    public Organization create(final String email, final String name, final String social1, final String social2, final String desc, final Institution inst) {
        final Organization o = new Organization(email, name, social1, social2, desc, inst);
        em.persist(o);
        return o;
    }

    private List<User> getUsersForRole(final Organization org, final Role role) {
        final String hql = "SELECT ur.user FROM UsersRoles ur WHERE ur.organization = :org AND ur.role = :role";
        return em.createQuery(hql, User.class).setParameter("org", org).setParameter("role", role).getResultList();
    }

    @Override
    public List<User> getPublishers(final Organization org) {
        return getUsersForRole(org, Role.PUBLISHER);
    }

    @Override
    public List<User> getManagers(final Organization org) {
        return getUsersForRole(org, Role.MANAGER);
    }

    @Override
    public boolean existsEmail(final String email) {
        final String hql = "SELECT COUNT(o) FROM Organization o WHERE o.orgEmail = :email";
        return em.createQuery(hql, Long.class).setParameter("email", email).getSingleResult() > 0;
    }
}
