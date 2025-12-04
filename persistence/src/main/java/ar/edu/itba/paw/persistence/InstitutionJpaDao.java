package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.InstitutionDao;
import ar.edu.itba.paw.interfaces.persistence.InvalidStateException;
import ar.edu.itba.paw.model.Boundaries;
import ar.edu.itba.paw.model.Institution;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.schemas.InstitutionSchema;
import ar.edu.itba.paw.model.schemas.OrganizationSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InstitutionJpaDao implements InstitutionDao {
    @PersistenceContext private EntityManager em;
    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionJpaDao.class);

    private static class NativeQueries {
        private static final String FIND_ALL_ORDERED =
            "SELECT i." + InstitutionSchema.ID + " FROM " + InstitutionSchema.TABLE_NAME + " AS i" +
                " ORDER BY i." + InstitutionSchema.ID +
                " OFFSET :offset LIMIT :limit";

        private static final String FIND_ALL_ORGANIZATIONS_ORDERED =
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
    public Optional<Institution> findByID(final long institutionID) {
        return Optional.ofNullable(em.find(Institution.class, institutionID));
    }

    @Override
    public Optional<Institution> findByDomainName(final String domainName) {
        final String hql = "FROM Institution i WHERE i.domainName = :dName";
        return em.createQuery(hql, Institution.class).setParameter("dName", domainName).getResultList().stream().findFirst();
    }

    @Override
    public Optional<Institution> findByAdminEmail(final String adminEmail) {
        final String hql = "FROM Institution i WHERE i.adminEmail = :email";
        return em.createQuery(hql, Institution.class).setParameter("email", adminEmail).getResultList().stream().findFirst();
    }

    @Override
    public List<Institution> findAll(final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> institutionIDs = executeNativeQuery(NativeQueries.FIND_ALL_ORDERED, nativeQueryParameters);
        final String hql = "FROM Institution i WHERE i.institutionID IN (:institutionIDs)";
        return em.createQuery(hql, Institution.class).setParameter("institutionIDs", institutionIDs).getResultList();
    }

    @Override
    public Institution create(final String name, final String domainName, final String adminEmail) {
        final Institution i = new Institution(name, domainName, adminEmail);
        em.persist(i);
        return i;
    }

    @Override
    public List<Organization> getOrganizations(final Institution inst, final int pageNum) {
        final Map<String, Object> nativeQueryParameters = new HashMap<>(Map.of(
            "institutionID", inst.getInstitutionID(),
            "offset", pageNum * Boundaries.PAGE_SIZE,
            "limit", Boundaries.PAGE_SIZE
        ));
        final List<Long> orgIDs = executeNativeQuery(NativeQueries.FIND_ALL_ORGANIZATIONS_ORDERED, nativeQueryParameters);
        final String hql = "FROM Organization o WHERE o.orgID IN (:orgIDs)";
        return em.createQuery(hql, Organization.class).setParameter("orgIDs", orgIDs).getResultList();
    }

    @Override
    public User getAdministrator(final Institution inst) {
        final String hql = "FROM User u WHERE u.userEmail = :email";
        return em.createQuery(hql, User.class).setParameter("email", inst.getAdminEmail()).getResultList().stream()
                .findFirst().orElseThrow(() -> {
                    LOGGER.error("User not found with email {}", inst.getAdminEmail());
                    return new InvalidStateException("InstitutionJpaDao -> getAdministrator");
                });
    }

    @Override
    public boolean existsDomainName(final String domainName) {
        final String hql = "SELECT COUNT(i) FROM Institution i WHERE i.domainName = :domainName";
        return em.createQuery(hql, Long.class).setParameter("domainName", domainName).getSingleResult() > 0;
    }

    @Override
    public boolean existsAdminEmail(final String adminEmail) {
        final String hql = "SELECT COUNT(i) FROM Institution i WHERE i.adminEmail = :adminEmail";
        return em.createQuery(hql, Long.class).setParameter("adminEmail", adminEmail).getSingleResult() > 0;
    }
}
