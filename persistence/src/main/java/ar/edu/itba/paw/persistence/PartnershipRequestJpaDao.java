package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.PartnershipRequestDao;
import ar.edu.itba.paw.model.enums.Status;
import ar.edu.itba.paw.model.PartnershipRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class PartnershipRequestJpaDao implements PartnershipRequestDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Optional<PartnershipRequest> findByID(final long requestID) {
        return Optional.ofNullable(em.find(PartnershipRequest.class, requestID));
    }


    @Override
    public List<PartnershipRequest> findAll() {
        final String hql = "FROM PartnershipRequest pr ORDER BY pr.requestDate DESC";
        return em.createQuery(hql, PartnershipRequest.class).getResultList();
    }

    @Override
    public List<PartnershipRequest> findByStatus(final Status status) {
        final String hql = "FROM PartnershipRequest pr WHERE pr.status = :status ORDER BY pr.requestDate DESC";
        return em.createQuery(hql, PartnershipRequest.class).setParameter("status", status).getResultList();
    }

    @Override
    public PartnershipRequest create(final String name, final String dName, final String adminEmail) {
        final PartnershipRequest pr = new PartnershipRequest(name, dName, adminEmail, Status.PENDING, LocalDateTime.now());
        em.persist(pr);
        return pr;
    }

    @Override
    public void updateStatus(final PartnershipRequest pr, final Status status) {
        pr.setStatus(status);
        pr.setProcessedDate(LocalDateTime.now());
    }

    @Override
    public boolean existsByDomainName(final String domainName) {
        final String hql = "SELECT COUNT(pr) FROM PartnershipRequest pr WHERE pr.domainName = :domainName";
        return em.createQuery(hql, Long.class).setParameter("domainName", domainName).getSingleResult() > 0;
    }
}
