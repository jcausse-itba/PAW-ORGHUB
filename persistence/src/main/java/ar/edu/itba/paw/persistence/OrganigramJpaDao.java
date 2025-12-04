package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.OrganigramDao;
import ar.edu.itba.paw.model.Organigram;
import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.enums.OpenPositionStatus;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class OrganigramJpaDao implements OrganigramDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Optional<Organigram> findByID(final long organigramID) {
        final String hql = "SELECT o FROM Organigram o LEFT JOIN FETCH o.assignedUser WHERE o.organigramID = :organigramID";
        return em.createQuery(hql, Organigram.class)
                .setParameter("organigramID", organigramID)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Organigram> findByOrganization(final Organization organization) {
        final String hql = "SELECT o FROM Organigram o LEFT JOIN FETCH o.assignedUser WHERE o.organization = :organization ORDER BY o.level, o.sortOrder";
        return em.createQuery(hql, Organigram.class).setParameter("organization", organization).getResultList();
    }

    @Override
    public List<Organigram> findByOrganizationAndLevel(final Organization organization, final int level) {
        final String hql = "FROM Organigram o WHERE o.organization = :organization AND o.level = :level ORDER BY o.sortOrder";
        return em.createQuery(hql, Organigram.class)
                .setParameter("organization", organization)
                .setParameter("level", level)
                .getResultList();
    }

    @Override
    public List<Organigram> findRootPositionsByOrganization(final Organization organization) {
        final String hql = "FROM Organigram o WHERE o.organization = :organization AND o.parentPosition IS NULL ORDER BY o.sortOrder";
        return em.createQuery(hql, Organigram.class).setParameter("organization", organization).getResultList();
    }

    @Override
    public List<Organigram> findChildPositions(final Organigram parentPosition) {
        final String hql = "FROM Organigram o WHERE o.parentPosition = :parentPosition ORDER BY o.sortOrder";
        return em.createQuery(hql, Organigram.class).setParameter("parentPosition", parentPosition).getResultList();
    }

    @Override
    public Organigram create(final Organization organization, final String positionName, final String description, final Organigram parentPosition, final int level, final int sortOrder) {
        final Organigram organigram = new Organigram(
            organization,
            positionName,
            description,
            parentPosition,
            level,
            sortOrder,
            null
        );
        em.persist(organigram);
        em.flush();
        return organigram;
    }

    @Override
    public void updatePosition(final Organigram position, final String positionName, final String description, final Organigram parentPosition, final int level, final int sortOrder) {
        position.setPositionName(positionName);
        position.setDescription(description);
        position.setParentPosition(parentPosition);
        position.setLevel(level);
        position.setSortOrder(sortOrder);
    }

    @Override
    public void delete(final Organigram position) {
        em.remove(position);
    }

    @Override
    public boolean existsPositionNameInOrganization(final String positionName, final Organization organization) {
        final String hql = "SELECT COUNT(o) FROM Organigram o WHERE o.positionName = :positionName AND o.organization = :organization";
        return em.createQuery(hql, Long.class)
                .setParameter("positionName", positionName)
                .setParameter("organization", organization)
                .getSingleResult() > 0;
    }

    @Override
    public boolean hasChildPositions(final Organigram position) {
        final String hql = "SELECT COUNT(o) FROM Organigram o WHERE o.parentPosition = :position";
        return em.createQuery(hql, Long.class)
                .setParameter("position", position)
                .getSingleResult() > 0;
    }

    @Override
    public boolean existsSortOrderInLevel(final int level, final int sortOrder, final Organization organization) {
        final String hql = "SELECT COUNT(o) > 0 FROM Organigram o WHERE o.level = :level AND o.sortOrder = :sortOrder AND o.organization = :organization";
        return em.createQuery(hql, Boolean.class)
                .setParameter("level", level)
                .setParameter("sortOrder", sortOrder)
                .setParameter("organization", organization)
                .getSingleResult();
    }

    @Override
    public boolean existsSortOrderInParent(final Organigram parentPosition, final int sortOrder, final Organization organization) {
        if (parentPosition == null) {
            final String hql = "SELECT COUNT(o) > 0 FROM Organigram o WHERE o.parentPosition IS NULL AND o.sortOrder = :sortOrder AND o.organization = :organization";
            return em.createQuery(hql, Boolean.class)
                    .setParameter("sortOrder", sortOrder)
                    .setParameter("organization", organization)
                    .getSingleResult();
        }
        final String hql = "SELECT COUNT(o) > 0 FROM Organigram o WHERE o.parentPosition = :parentPosition AND o.sortOrder = :sortOrder AND o.organization = :organization";
        return em.createQuery(hql, Boolean.class)
                .setParameter("parentPosition", parentPosition)
                .setParameter("sortOrder", sortOrder)
                .setParameter("organization", organization)
                .getSingleResult();
    }

    @Override
    public void assignUserToPosition(final Organigram position, final User user) {
        position.setAssignedUser(user);
    }

    @Override
    public void unassignUserFromPosition(final Organigram position) {
        position.setAssignedUser(null);
    }

    @Override
    public List<Organigram> findByUser(final User user) {
        final String hql = "SELECT DISTINCT o FROM Organigram o LEFT JOIN FETCH o.organization WHERE o.assignedUser = :user";
        return em.createQuery(hql, Organigram.class).setParameter("user", user).getResultList();
    }

    @Override
    public List<Organigram> findByUserAndOrganization(final User user, final Organization organization) {
        final String hql = "SELECT DISTINCT o FROM Organigram o LEFT JOIN FETCH o.organization WHERE o.assignedUser = :user AND o.organization = :organization";
        return em.createQuery(hql, Organigram.class)
                .setParameter("user", user)
                .setParameter("organization", organization)
                .getResultList();
    }

    @Override
    public List<Organigram> findVacantPositionsByOrganization(final Organization organization) {
        final String hql = "SELECT DISTINCT o FROM Organigram o LEFT JOIN FETCH o.organization " +
                "WHERE o.organization = :organization " +
                "AND o.assignedUser IS NULL " +
                "AND NOT EXISTS (" +
                "    SELECT 1 FROM OpenPosition op " +
                "    WHERE op.organigramPosition = o " +
                "    AND op.status = :activeStatus" +
                ") " +
                "ORDER BY o.level, o.sortOrder";
        return em.createQuery(hql, Organigram.class)
                .setParameter("organization", organization)
                .setParameter("activeStatus", OpenPositionStatus.ACTIVE)
                .getResultList();
    }
}
